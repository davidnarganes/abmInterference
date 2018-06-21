/* Created by David Narganes on 01/05/2018.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import sim.engine.SimState;
import sim.engine.Steppable;
import sim.field.continuous.Continuous2D;
import sim.field.network.Edge;
import sim.util.Bag;
import sim.util.Double2D;
import sim.util.MutableDouble2D;

/** PATIENT CLASS
 * The Patient object will be defined in this class
 * The Patient will represent the object of the simulation
 */

public class Patient implements Steppable {
    private boolean infected;
    private boolean vaccine;
    private boolean sex;
    private int degree;
    private double contagionDistance;
    private double infectiousnessDistance;
    private double indirectInferference;
    private String name;

    // LABEL TO DISPLAY IN SIM WHEN SELECTED
    public String toString(){
        return "[" + System.identityHashCode(this) +
                "]\nCD:" + getContagionDistance() +
                "\nInfected:" + getInfected() +
                "\nVaccine:" + getVaccine() +
                "\nSex:" + getSex() +
                "\nDegree:" + getDegree();
    }

    /** PATIENT CONSTRUCTOR
     * Here the properties of the object patient will be defined:
     * 1. Infection status
     * 2. Vaccine 
     * 3. Sex
     * 4. Cumulative Infected Distance
     * 5.Name 
     * @param name to specify a name per agent and be recognisable though the course of the simulation
     * @param city that is required to generate the random integer for the sex
     */
    public Patient (String name, City city){
        this.setInfected(false);
        this.setVaccine(false);
        this.setSex(city.random.nextBoolean());
        this.setDegree(0);
        this.setContagionDistance(0.0);
        this.setInfectiousnessDistance(0.0);
        this.setIndirectInferference(0.0);
        this.setName(name);
    }

    // Setters
    private void setSex(boolean bool){
        sex = bool;
    }
    private void setInfected(boolean bool){
        infected = bool;
    }
    private void setVaccine(boolean bool) {
        vaccine = bool;
    }
    private void setName(String string){
        name = string;
    }
    private void setDegree(int value){degree = value;}
    private void setContagionDistance(double value){
        contagionDistance = value;
    }
    private void setInfectiousnessDistance(double value){infectiousnessDistance = value;}
    private void setIndirectInferference(double value){indirectInferference = value;}

    // Getters
    public boolean getSex(){
        return sex;
    }
    public boolean getInfected(){
        return infected;
    }
    public boolean getVaccine(){
        return vaccine;
    }
    public String getName(){
        return name;
    }
    public int getDegree(){return degree;}
    public double getContagionDistance(){
        return contagionDistance;
    }
    public double getInfectiousnessDistance(){return infectiousnessDistance;}
    public double getIndirectInferference(){return indirectInferference;}


    /** STEP METHOD
     * All the defined methods/functions will be applied here to be applied at each step of the simulation
     * The simulation will be finalised if it meets the condition of earlyGUIStop
     * @param state to get the current state of the simulation
     */
    public void step(SimState state){
        City city = (City) state;
        long step = city.schedule.getSteps();

        // INITIATE NETWORK
        if (step == 0){
            defineNetwork(city);
            actualiseDegree(city);
        }

        // CHANGE NETWORK EACH X STEPS
        changeNetwork(city);

        // test
        for(int i = 0; i < 5; i++){
            System.out.println(PoissonCDF(i,city.getLambda()));
        }

        // ACTUALISE LOCATION OF AGENTS
        actualiseLocation(city);

        // ACTUALISE PROBABILITY AT EACH STEP
        apply_vaccine(city);
        apply_infection(city);

        // FINISH THE SIMULATION
        if (step == city.earlyGUIStop){
            city.finish();
        }
    }

    /** CHANGE FRIEND/ENEMY NETWORK
     * At every step of the simulation there will be a probability of changing the prior set of peers
     * and enemies to ensure that all agents can interact among each other by:
     * 1. Defining a set of agents that will be FRIENDS to each individual agent
     * 2. Defining a set of agents that will be ENEMIES to each individual agent
     * @param city to get the current state of the agents
     */

    public void defineNetwork(City city){
        Object other;
        Bag peers = city.peers.getAllNodes();
        Bag edges = city.peers.getEdges(this, new Bag());

        // Clean ALL previous edges
        for(int i = 0; i < edges.size(); i++){
            city.peers.removeEdge((Edge) edges.get(i));
        }

        // Iterate though peers and add edge based on Poisson probability
        for(int i = 0; i < peers.size(); i++) {
            other = (Patient) peers.get(i);

            if (city.random.nextDouble() < PoissonCDF(this.getDegree() + ((Patient) other).getDegree(), city.getLambda())){
                if (this != other) {
                    double peership = city.random.nextDouble();
                    city.peers.addEdge(this, other, peership);

                    // Actualise degree of this and other
                    this.setDegree(this.getDegree() + 1);
                    ((Patient) other).setDegree(((Patient) other).getDegree() + 1);
                }
            }
        }
    }

    private void actualiseDegree(City city){
        this.setDegree(city.peers.getEdges(this, new Bag()).size());
    }

    /** ACTUALISE LOCATION OF THE AGENTS AT EACH STEP
     * The agents interact with each other at every step based on:
     * 1. The number of FRIENDS they have in the network at each step
     * 2. The number of ENEMIES they have in the network at each step
     * 3. A force that is guiding the patients to remain in the center of the simulation
     * 4. A random force whose magnitude can be specified
     * 5. The prior location of the agent
     * After adding up all these forces, the agents will be placed at their new locations
     * @param city to get the current state of the agents
     */

    private void actualiseLocation(City city){
        Continuous2D yard = city.yard;
        Double2D ego;
        Double2D alter;
        Double2D forceCentre;
        Double2D forceRandom;

        ego = city.yard.getObjectLocation(this);

        // Define forces
        MutableDouble2D sumForces = new MutableDouble2D();
        MutableDouble2D forcePartner = new MutableDouble2D();

        forceCentre = new Double2D((yard.width * 0.5 - ego.x) * city.getForceCenter(),
                (yard.height * 0.5 - ego.y) * city.getForceCenter());
        forceRandom = new Double2D(city.getRandomMultiplier() * city.random.nextDouble() - city.getRandomMultiplier() * 0.5,
                city.getRandomMultiplier() * city.random.nextDouble() - city.getRandomMultiplier() * 0.5);

        Bag out = city.peers.getEdges(this,null);

        for (int i = 0; i < out.size(); i++){

            Edge edge = (Edge) out.get(i);

            double peership = ((Double) edge.info).doubleValue() * city.getPartnerMultiplier();

            // COULD BE THE TO() END OR THE FROM() END
            alter = city.yard.getObjectLocation(edge.getOtherNode(this));

            if(peership >= 0){
                forcePartner.setTo((alter.x - ego.x) * peership,
                        (alter.y - ego.y) * peership);

                if(forcePartner.length() > city.getMaxPartnerForce()){
                    forcePartner.resize(city.getMaxPartnerForce());
                }
            } else {
                forcePartner.setTo((alter.x - ego.x) * -peership,
                        (alter.y - ego.y) * -peership);
                if(forcePartner.length() > city.getMaxPartnerForce()){
                    forcePartner.resize(0.0);
                }
                else if (forcePartner.length() > 0){
                    forcePartner.resize(city.getMaxPartnerForce() - forcePartner.length());
                }
            }
        }

        // Sum all forces
        sumForces.addIn(forcePartner);
        sumForces.addIn(forceCentre);
        sumForces.addIn(forceRandom);
        sumForces.addIn(ego);

        // Actualise location
        city.yard.setObjectLocation(this, new Double2D(sumForces));
    }

    /** CALCULATE CUMULATIVE DISTANCE
     * To determine how close is each agent with regards to the infected agents
     * @param city to get the agents
     * @return the cumulative distance between each agent and all the infected agents
     */
    private double calculateCumDistance(City city){
        Bag agents = city.peers.getAllNodes();
        double cumulativeVector = 0.0;
        Patient current;
        for(int i = 0; i < agents.size(); i++){
            current = (Patient) agents.get(i);
            if(current.getInfected() && current != this){
                Double2D one = city.yard.getObjectLocation(this);
                Double2D other = city.yard.getObjectLocation(current);
                cumulativeVector += (1 - city.getInfectiousness() * current.getAllVaccine())/(1 + distance(one,other));
            }
        }
        setIndirectInferference(cumulativeVector);
        return cumulativeVector;
    }

    /** COUNT NUMBER OF INFECTED
     * At every step of the simulation is important to determine how many patients have the property infected
     * equal to 1
     * @param city to get the current state of the agents
     * @return count of the number of patient whose state is infected
     */

    public int count_infected(City city){
        Bag agents = city.peers.getAllNodes();
        Patient current;
        int count = 0;
        for(int i = 0; i < agents.size(); i++){
            current = (Patient) agents.get(i);
            if (current.getInfected()){
                count++;
            }
        }
        return count;
    }

    /** COUNT NUMBER OF VACCINATED
     * At every step of the simulation is important to determine how many patients have the property vaccinated
     * equal to 1
     * @param city to get the current state of the agents
     * @return count of the number of patient whose state is infected
     */

    public int count_vaccinated(City city){
        Bag agents = city.peers.getAllNodes();
        Patient current;
        int count = 0;
        for(int i = 0; i < agents.size(); i++){
            current = (Patient) agents.get(i);
            if (current.getVaccine()){
                count++;
            }
        }
        return count;
    }

    /** GET ALL SEX
     * Method to get the information about the sex all agents
     * @return is a list of the sex information
     */
    private int getAllSex(){
        return this.getSex() ? 1:0;
    }

    /** GET ALL INFECTED
     * Method to get the information about the infection of all agents
     * @return is a list of the infection information
     */
    private int getAllInfected(){
        return this.getInfected() ? 1:0;
    }

    /** GET ALL TREATMENT
     * Method to get the information about the vaccine of all agents
     * @return is a list of the vaccine information
     */

    private int getAllVaccine(){
        return this.getVaccine() ? 1:0;
    }

    /** PROBABILITY OF CHANGING THE NETWORK
     * At every step there will be a probability of changing the group of peers/enemies of each agent to ensure
     * that every patient has the probability of interacting with each other patient
     * @param city to import the random number generator
     */
    private void changeNetwork(City city){
        double probChangeNetwork = city.random.nextDouble();
        if(probChangeNetwork < city.getPromiscuityPopulation()){
            defineNetwork(city);
        }
        actualiseDegree(city);
    }

    /** GENERATE TREATMENTS
     * The probability of accessing the vaccine will just depend on:
     * 1. The Sex as confounder
     * 2. The basal probability of getting vaccine
     * 3. The outcome
     * @param city to get the pseudo-random number generator
     */
    private void apply_vaccine(City city){
        double rnd = city.random.nextDouble();
        int confounding_sex = this.getSex() ? 1:0;
        double apply_vaccine = (1 + city.getSexOnVaccine() * confounding_sex) * city.getProbVaccine();
        if(rnd < apply_vaccine){
            setVaccine(true);
        }
    }

    /** GENERATE INFECTIONS
     * The probability of getting infected depends on:
     * 1. Sex value
     * 2. Treatment value
     * 3. Basal probability of getting infected
     * 4. Percentage of infected patients at the prior simulation state
     * 5. Cumulative distance of each patient to the infected patients
     * @param city to get the pseudo-random number generator
     */
    private void apply_infection(City city){
        double rnd = city.random.nextDouble();
        int cause_sex = getAllSex();
        int cause_vaccine = getAllVaccine();
        int n_infected = count_infected(city);
        double cum_distance = calculateCumDistance(city);
        double transmission = city.getContagion()* n_infected / (1 + cum_distance);
        double apply_infection = (1 + city.getSexOnInfection() * cause_sex -
                city.getVaccineOnInfection()*cause_vaccine +
                transmission) * city.getProbInfected();
        if(rnd < apply_infection){
            setInfected(true);
        }
    }

    /** EUCLIDEAN DISTANCE
     * To calculate the Euclidean distance between agents in the simulation
     * @param one is the location of a current agent
     * @param other is the location of any other agent that will be iterated though a loop
     * @return the Euclidean distance
     */

    private double distance (Double2D one, Double2D other){
        double y = Math.abs(other.y - one.y);
        double x = Math.abs(other.x - one.x);
        return Math.sqrt(y*y + x*x);
    }

    /** POISSON CUMULATIVE DENSITY FUNCTION: LEFT
     * To calculate the left cumulative probability
     * @param x point
     * @param lambda mean of the function and variance
     * @return the cdf
     */
    private double PoissonCDF(int x, double lambda){
        double result = 0.0;
        int i = 0;
        while (i < x) {
            result = result + Poisson(i, lambda);
            i = i + 1;
        }
        return 1 - result;
    }

    /**
     * def poissonPDF(x,lambdaa):
     *     get = i = 0
     *     while i < x:
     *         get = get + f(i,lambdaa)
     *         i = i + 1
     *     return 1 - get
     */

    /** POISSON FUNCTION
     * @param x point
     * @param lambda as defined
     * @return the probability
     */
    private double Poisson(int x, double lambda){
        return (Math.pow(lambda, x) * Math.exp(-lambda)) / factorial(x);
    }

    /** FACTORIAL FUNCTION
     * @param N to calculate N!
     * @return factorial of N
     */
    private static long factorial(int N) {
        long multi = 1;
        for (int i = 1; i <= N; i++) {
            multi = multi * i;
        }
        return multi;
    }
}
