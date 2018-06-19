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

import sim.engine.*;
import sim.field.continuous.Continuous2D;
import sim.field.network.Network;
import sim.util.Bag;
import sim.util.Double2D;
import sim.util.Interval;
import java.io.File;
import java.util.Scanner;

/** CITY CLASS
 * This class will extend the SimState from Mason
 * The city will initialise the simulation by creating:
 * 1. The continuous space that will be defined as yard
 * 2. The patients that will be the agents of the simulation, defined in the n_patients
 * 3. The network to ensure that all patients can potentially interact with each other
 */

public class City extends SimState{
    public Continuous2D yard = new Continuous2D(1.0,80,80);

    // Default parameters
    private int numPatients = 200;
    private double probInfected = 0.0001;
    private double probVaccine = 0.0003;
    private double contagion = 0.5;
    private double infectiousness = 0.5;
    private int sexOnVaccine = 3;
    private int sexOnInfection = 3;
    private int vaccineOnInfection = 3;
    private double promiscuityPopulation = 0.01;
    private double maxPartnerForce = 20.0;
    private double forceCenter = 0.0;
    private double randomMultiplier = 10.0;
    private double partnerMultiplier = 10.0;
    private Scanner inputStream;
    private int lines = 0;
    private boolean multiSIM = true;
    public int earlyGUIStop = 200;
    private static int numIntervals = 1;
    public Network peers = new Network(false);

    /** CITY CONSTRUCTOR
     * @param seed for seeding a pseudo-random number generator
     * Set all the properties of the City Simulation
     */

    public City(long seed){
        super(seed);
        this.setNumPatients(numPatients);
        this.setProbInfected(probInfected);
        this.setProbVaccine(probVaccine);
        this.setContagion(contagion);
        this.setSexOnVaccine(sexOnVaccine);
        this.setSexOnInfection(sexOnInfection);
        this.setVaccineOnInfection(vaccineOnInfection);
        this.setPromiscuityPopulation(promiscuityPopulation);
    }

    // Lots of getters and setters to change them in the SIM
    public int getNumPatients() {
        return numPatients;
    }
    public void setNumPatients(int patients) {
        if (patients >= 0){
            numPatients = patients;
        }
    }
    public Object domNumPatients() {
        return new Interval(1, 5000);
    }
    public double getProbInfected() {
        return probInfected ;
    }
    public void setProbInfected(double value) {
        if (value >= 0.0){
            probInfected = value;
        }
    }
    public Object domProbInfected() {
        return new Interval(0.0, 1.0);
    }
    public double getProbVaccine() {
        return probVaccine ;
    }
    public void setProbVaccine(double value) {
        if (value >= 0.0){
            probVaccine = value;
        }
    }
    public Object domProbVaccine() {
        return new Interval(0.0, 1.0);
    }
    public double getContagion(){
        return contagion;
    }
    public void setContagion(double value){
        if (value >= 0.0){
            contagion = value;
        }
    }
    public Object domContagion(){
        return new Interval(0.0,10.0);
    }
    public int getSexOnVaccine(){
        return sexOnVaccine;
    }
    public void setSexOnVaccine(int value){
        if (value >= 0){
            sexOnVaccine = value;
        }
    }
    public Object domSexOnVaccine(){
        return new Interval(0,10);
    }
    public int getSexOnInfection(){
        return sexOnInfection;
    }
    public void setSexOnInfection(int value){
        if (value >= 0){
            sexOnInfection = value;
        }
    }
    public Object domSexOnInfection(){
        return new Interval(0,10);
    }
    public int getVaccineOnInfection(){
        return vaccineOnInfection;
    }
    public void setVaccineOnInfection(int value){
        if (value >= 0){
            vaccineOnInfection = value;
        }
    }
    public Object domVaccineOnInfection(){
        return new Interval(0,10);
    }
    public double getPromiscuityPopulation(){
        return promiscuityPopulation;
    }
    public void setPromiscuityPopulation(double value){
        if (value >= 0.0){
            promiscuityPopulation = value;
        }
    }
    public Object domPromiscuityPopulation(){
        return new Interval(0.0,1.0);
    }
    public double getMaxForce(){
        return maxPartnerForce;
    }
    public void setMaxForce(double value){
        maxPartnerForce = value;
    }
    public Object domMaxForce(){
        return new Interval(0.0,20.0);
    }
    public double getForceCenter(){
        return forceCenter;
    }
    public void setForceCenter(double value){
        forceCenter = value;
    }
    public Object domForceCenter(){
        return new Interval(-1.0, 20.0);
    }
    public double getRandomMultiplier(){
        return randomMultiplier;
    }
    public void setRandomMultiplier(double value){
        randomMultiplier = value;
    }
    public Object domRandomMultiplier()

    {
        return new Interval(0.0,20.0);
    }
    public double getInfectiousness(){
        return infectiousness;
    }
    public void setInfectiousness(double value){
        infectiousness = value;
    }
    public Object domInfectiousness(){
        return new Interval(0.0,1.0);
    }
    public double getPartnerMultiplier(){return partnerMultiplier;}
    public void setPartnerMultiplier(double value){partnerMultiplier = value;}
    public Object domPartnerMultiplier(){return new Interval(1.0,20.0);}

    // GETTER SETTER LINES
    private int getLines(){
        return lines;
    }
    private void setLines(int value){
        lines = value;
    }


    /** GET DISTRUBUTION OF TIME-DEPENDENT VARIABLES IN GUI
     * The mason library will automatically detect this methods and iteratively generate
     * histograms of the distribution of
     * Infection
     * Vaccines
     * Cumulative infected distance
     * at each timepoint
     * @return distribution of the variables
     */

    // INFECTION DISTRIBUTION

    public boolean[] getInfectionDistribution(){
      Bag agents = peers.getAllNodes();
      boolean[] distribution = new boolean[agents.numObjs];
      for(int i = 0; i < agents.numObjs; i++){
          distribution[i] = ((Patient)(agents.objs[i])).getInfected();
      }
      return distribution;
    }

    // VACCINE DISTRIBUTION

    public boolean[] getVaccineDistribution(){
        Bag agents = peers.getAllNodes();
        boolean[] distribution = new boolean[agents.numObjs];
        for(int i = 0; i < agents.numObjs; i++){
            distribution[i] = ((Patient)(agents.objs[i])).getVaccine();
        }
        return distribution;
    }

    // CUMULATIVE INFECTION DISTANCE DISTRIBUTION

    public double[] getCumulativeDistanceDistribution(){
        Bag agents = peers.getAllNodes();
        double[] distribution = new double[agents.numObjs];
        for(int i = 0; i < agents.numObjs; i++){
            distribution[i] = ((Patient)(agents.objs[i])).getCumulativeDistance();
        }
        return distribution;
    }

    public int[] getEdgesDegreeDistribution(){
        Bag peers = this.peers.getAllNodes();
        int[] distribution = new int[peers.numObjs];

        for (int i = 0; i < peers.size(); i++){
            Bag edges = this.peers.getEdges(peers.get(i), new Bag());
            distribution[i] = edges.size();
        }
        return distribution;
    }
    /** START
     * The simulation will initialise
     * The yard and network will be cleared while the agents are created and scheduled, stepped
     * The state of the simulation will be saved at each step
     */

    public void start(){
        super.start();
        int initialLocationMultiplier = 40;

        yard.clear();
        peers.clear();

        // ADD PATIENTS TO THE YARD
        for (int i = 0; i < this.getNumPatients(); i++){
            Patient patient = new Patient("Patient_"+i, this);
            yard.setObjectLocation(patient, new Double2D(yard.getWidth() * 0.5 +
                    initialLocationMultiplier * random.nextDouble() -
                    initialLocationMultiplier * 0.5,
                    yard.getHeight() * 0.5 +
                            initialLocationMultiplier * random.nextDouble() -
                            initialLocationMultiplier * 0.5));
            peers.addNode(patient);
            schedule.scheduleRepeating(patient);
        }

        // CREATE FILE TO SAVE STATE
        Utils utils = new Utils(this);
        schedule.scheduleRepeating(utils, 0, numIntervals);
    }

    /** ReadFile Method
     * The columns of the `params` file will be stored as properties of a FileParams object
     * @return A FileParams object that will contain the columns of the file as properties
     */
    private FileParams readFile(){

        try{
            inputStream = new Scanner(new File("params"));
        } catch (Exception e){
            System.out.println("Could not find file");
        }

        // Ignore the first line: HEADER
        inputStream.next();

        // Initialise the object to collect parameters
        FileParams fileParams = new FileParams();

        int index = 1;

        while(inputStream.hasNext()) {
            String allData = inputStream.next();
            String[] columns = allData.split(",");

            fileParams.addProbInfected(Double.parseDouble(columns[0]));
            fileParams.addProbVaccine(Double.parseDouble(columns[1]));
            fileParams.addContagion(Double.parseDouble(columns[2]));
            fileParams.addInfectiousness(Double.parseDouble(columns[3]));
            fileParams.addSexOnInfection(Integer.parseInt(columns[4]));
            fileParams.addSexOnVaccine(Integer.parseInt(columns[5]));
            fileParams.addVaccineOnInfection(Integer.parseInt(columns[6]));
            fileParams.addPromiscuityPopulation(Double.parseDouble(columns[7]));
            this.setLines(index++);
        }

        inputStream.close();

        return fileParams;
    }

    public static void main (String[] args) {

        // Define vars
        SimState state;
        boolean multiSIM;

        // State vars
        state= new City(System.currentTimeMillis());
        multiSIM = ((City) state).multiSIM;

        if (multiSIM){

            System.out.println("multiSIM MODE");
            FileParams fileParams;
            fileParams = ((City) state).readFile();

            for (int i = 0; i < ((City) state).getLines(); i++){

                // Define params of SIM at each iteration
                ((City) state).setProbInfected(fileParams.getProbInfected(i));
                ((City) state).setProbVaccine(fileParams.getProbVaccine(i));
                ((City) state).setContagion(fileParams.getContagion(i));
                ((City) state).setContagion(fileParams.getInfectiousness(i));
                ((City) state).setSexOnInfection(fileParams.getSexOnInfection(i));
                ((City) state).setSexOnVaccine(fileParams.getSexOnVaccine(i));
                ((City) state).setVaccineOnInfection(fileParams.getVaccineOnInfection(i));
                ((City) state).setPromiscuityPopulation(fileParams.getPromiscuityPopulation(i));

                // RUN THE SIM
                state.start();
                System.out.println("Executing SIM "+ i + "...");
                do {
                    if (!state.schedule.step(state)) break;
                }
                while(state.schedule.getSteps() < ((City) state).earlyGUIStop);
                state.finish();
            }

            System.exit(0);

        } else {
            System.out.println("uniSIM MODE");
            state.start();
            do {
                if (!state.schedule.step(state)) break;
            }
            while(state.schedule.getSteps() < ((City) state).earlyGUIStop);
            state.finish();
            System.exit(0);
        }
    }
}