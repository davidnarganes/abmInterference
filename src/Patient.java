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
    private boolean treatment;
    private boolean sex;
    private double cumulativeDistance;
    private String name;

    // LABEL TO DISPLAY IN SIM WHEN SELECTED
    public String toString(){
        return "[" + System.identityHashCode(this) +
                "]\nICD:" + getCumulativeDistance() +
                "\nInfected:" + getInfected() +
                "\nVaccine:" + getTreatment() +
                "\nSex:" + getSex();
    }

    /** PATIENT CONSTRUCTOR
     * Here the properties of the object patient will be defined:
     * 1. Infection status
     * 2. Treatment
     * 3. Sex
     * 4. Cumulative Infected Distance
     * 5. Name
     * @param name to specify a name per agent and be recognisable though the course of the simulation
     * @param city that is required to generate the random integer for the sex
     */
    public Patient (String name, City city){
        this.setInfected(false);
        this.setTreatment(false);
        this.setSex(city.random.nextBoolean());
        this.setCumulativeDistance(0.0);
        this.setName(name);
    }

    /** DEFINE SET/GET ATTRIBUTES
     * Methods to set or get the properties:
     * 1. Sex
     * 2. Infection state
     * 3. Treatment state
     * @param bool will be either a 0 or a 1 to indicate the value of the property
     */
    public void setSex(boolean bool){
        sex = bool;
    }
    public void setInfected(boolean bool){
        infected = bool;
    }
    public void setTreatment(boolean bool) {
        treatment = bool;
    }
    public void setName(String string){
        name = string;
    }
    public void setCumulativeDistance(double value){
        cumulativeDistance = value;
    }
    public boolean getSex(){
        return sex;
    }
    public boolean getInfected(){
        return infected;
    }
    public boolean getTreatment(){
        return treatment;
    }
    public double getCumulativeDistance(){
        return cumulativeDistance;
    }
    public String getName(){
        return name;
    }


    /** STEP METHOD
     * All the defined methods/functions will be applied here to be applied at each step of the simulation
     * The simulation will be finalised if it meets the condition of earlyStop
     * @param state to get the current state of the simulation
     */
    public void step(SimState state){
        City city = (City) state;

        // CHANGE NETWORK EACH X STEPS
        changeFriendNetwork(city);

        // ACTUALISE LOCATION OF AGENTS
        actualiseLocation(city);

        // ACTUALISE PROBABILITY AT EACH STEP
        apply_treatment(city);
        apply_infection(city);

        // FINISH THE SIMULATION
        long step = city.schedule.getSteps();
        if (step == city.earlyStop){
            city.finish();
        }
    }

    /** CHANGE FRIEND/ENEMY NETWORK
     * At every step of the simulation there will be a probability of changing the prior set of friends
     * and enemies to ensure that all agents can interact among each other by:
     * 1. Defining a set of agents that will be FRIENDS to each individual agent
     * 2. Defining a set of agents that will be ENEMIES to each individual agent
     * @param city to get the current state of the agents
     */

    private void changeFriend(City city){
        Object other;
        Bag friends = city.friends.getAllNodes();
        Bag edges = city.friends.getEdges(this, new Bag());

        for(int i = 0; i < edges.size(); i++){
            city.friends.removeEdge((Edge) edges.get(i));
        }

        // WHO LIKES?
        do
            other = friends.get(city.random.nextInt(friends.numObjs));
        while (this == other);
        double friendship = city.random.nextDouble();
        city.friends.addEdge(this,other,friendship);
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
        Double2D me = city.yard.getObjectLocation(this);

        // GO THROUGH FRIENDS AND DETERMINE: 1.HOW MUCH THEY WANNA BE NEAR, 2. CUMULATIVE DISTANCE
        MutableDouble2D sumForces = new MutableDouble2D();
        MutableDouble2D forceVector = new MutableDouble2D();

        Bag out = city.friends.getEdges(this,null);

        for (int i = 0; i < out.size(); i++){

            Edge edge = (Edge) out.get(i);

            double friendship = ((Double)(edge.info)).doubleValue();

            // COULD BE THE TO() END OR THE FROM() END
            Double2D him = city.yard.getObjectLocation(edge.getOtherNode(this));

            if(friendship >= 0){
                forceVector.setTo((him.x - me.x) * friendship,
                        (him.y - me.y) * friendship);
                if(forceVector.length() > city.getMaxForce()){
                    forceVector.resize(city.getMaxForce());
                }
            } else {
                forceVector.setTo((him.x - me.x) * friendship,
                        (him.y - me.y) * friendship);
                if(forceVector.length() > city.getMaxForce()){
                    forceVector.resize(0.0);
                }
                else if (forceVector.length() > 0){
                    forceVector.resize(city.getMaxForce() - forceVector.length());
                }
            }
        }

        sumForces.addIn(forceVector);

        // 3. FORCE TO CENTER VECTOR
        // 4. RANDOM VECTOR
        // 5. ME VECTOR: PRIOR LOCATION
        Double2D centreVector = new Double2D((yard.width * 0.5 - me.x) * city.getForceCenter(),
                (yard.height * 0.5 - me.y) * city.getForceCenter());
        Double2D randomVector = new Double2D(city.getRandomMultiplier() * city.random.nextDouble() - city.getRandomMultiplier() * 0.5,
                city.getRandomMultiplier() * city.random.nextDouble() - city.getRandomMultiplier() * 0.5);

        sumForces.addIn(centreVector);
        sumForces.addIn(randomVector);
        sumForces.addIn(me);
        city.yard.setObjectLocation(this, new Double2D(sumForces));
    }

    /** CALCULATE CUMULATIVE DISTANCE
     * To determine how close is each agent with regards to the infected agents
     * @param city to get the agents
     * @return the cumulative distance between each agent and all the infected agents
     */
    private double calculateCumDistance(City city){
        Bag agents = city.friends.getAllNodes();
        double cumulativeVector = 0.0;
        Patient current;
        for(int i = 0; i < agents.size(); i++){
            current = (Patient) agents.get(i);
            if(current.getInfected() && current != this){
                Double2D one = city.yard.getObjectLocation(this);
                Double2D other = city.yard.getObjectLocation(current);
                cumulativeVector += (1 - 0.5 * current.getAllTreatment())/distance(one,other);
            }
        }
        setCumulativeDistance(cumulativeVector);
        return cumulativeVector;
    }

    /** COUNT NUMBER OF INFECTED
     * At every step of the simulation is important to determine how many patients have the property infected
     * equal to 1
     * @param city to get the current state of the agents
     * @return count of the number of patient whose state is infected
     */

    public int count_infected(City city){
        Bag agents = city.friends.getAllNodes();
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
        Bag agents = city.friends.getAllNodes();
        Patient current;
        int count = 0;
        for(int i = 0; i < agents.size(); i++){
            current = (Patient) agents.get(i);
            if (current.getTreatment()){
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
     * Method to get the information about the treatment of all agents
     * @return is a list of the treatment information
     */

    private int getAllTreatment(){
        return this.getTreatment() ? 1:0;
    }

    /** PROBABILITY OF CHANGING THE NETWORK
     * At every step there will be a probability of changing the group of friends/enemies of each agent to ensure
     * that every patient has the probability of interacting with each other patient
     * @param city to import the random number generator
     */
    private void changeFriendNetwork(City city){
        double prob_changeFriend = city.random.nextDouble();
        if(prob_changeFriend < city.getPromiscuityPopulation()){
            changeFriend(city);
        }
    }

    /** GENERATE TREATMENTS
     * The probability of accessing the treatment will just depend on:
     * 1. The Sex as confounder
     * 2. The basal probability of getting treatment
     * 3. The outcome: no outcome, no treatment
     * @param city to get the pseudo-random number generator
     */
    private void apply_treatment(City city){
        double rnd = city.random.nextDouble();
        int confounding_sex = this.getSex() ? 1:0;
        double apply_treatment = (1 + city.getSexOverVaccine() * confounding_sex) * city.getProbVaccine();
        if(rnd < apply_treatment){
            setTreatment(true);
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
        int cause_treatment = getAllTreatment();
        int n_infected = count_infected(city);
        double cum_distance = calculateCumDistance(city);
        double transmission = city.getTransmissionEffect()* n_infected / (1 + cum_distance);
        double apply_infection = (1 + city.getSexOverInfection()*cause_sex -
                city.getVaccineOverInfection()*cause_treatment +
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
}
