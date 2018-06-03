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
 * This class will be a extension of the SimState from Mason
 * The city will initialise the simulation by creating:
 * 1. The continuous space that will be defined as yard
 * 2. The patients that will be the agents of the simulation, defined in the n_patients
 * 3. The network to ensure that all patients can potentially interact with each other
 */

public class City extends SimState{
    public Continuous2D yard = new Continuous2D(1.0,80,80);

    // Default parameters
    private int numPatients = 1000;
    private double probInfected = 0.0001;
    private double probVaccine = 0.0003;
    private double transmissionEffect = 0.5;
    private int sexOverVaccine = 3;
    private int sexOverInfection = 3;
    private int vaccineOverInfection = 3;
    private double promiscuityPopulation = 0.01;
    private double maxForce = 20.0;
    private double forceCenter = 0.0005;
    private double randomMultiplier = 10.0;
    private Scanner inputStream;
    private int lines = 0;
    private boolean multiSIM = true;

    public int earlyStop = 200;
    private int numIntervals = 1;
    public Network friends = new Network(false);

    /** CITY CONSTRUCTOR
     * @param seed for seeding a pseudo-random number generator
     * Set all the properties of the City Simulation
     */

    public City(long seed){
        super(seed);
        this.setNumPatients(numPatients);
        this.setProbInfected(probInfected);
        this.setProbVaccine(probVaccine);
        this.setTransmissionEffect(transmissionEffect);
        this.setSexOverVaccine(sexOverVaccine);
        this.setSexOverInfection(sexOverInfection);
        this.setVaccineOverInfection(vaccineOverInfection);
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
    public double getTransmissionEffect(){
        return transmissionEffect;
    }
    public void setTransmissionEffect(double value){
        if (value >= 0.0){
            transmissionEffect = value;
        }
    }
    public Object domTransmissionEffect(){
        return new Interval(0.0,10.0);
    }
    public int getSexOverVaccine(){
        return sexOverVaccine;
    }
    public void setSexOverVaccine(int value){
        if (value >= 0){
            sexOverVaccine = value;
        }
    }
    public Object domSexOverVaccine(){
        return new Interval(0,10);
    }
    public int getSexOverInfection(){
        return sexOverInfection;
    }
    public void setSexOverInfection(int value){
        if (value >= 0){
            sexOverInfection = value;
        }
    }
    public Object domSexOverInfection(){
        return new Interval(0,10);
    }
    public int getVaccineOverInfection(){
        return vaccineOverInfection;
    }
    public void setVaccineOverInfection(int value){
        if (value >= 0){
            vaccineOverInfection = value;
        }
    }
    public Object domVaccineOverInfection(){
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
        return maxForce;
    }
    public void setMaxForce(double value){
        maxForce = value;
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

    // GETTER SETTER LINES
    private int getLines(){
        return lines;
    }
    private void setLines(int value){
        lines = value;
    }


    // Get distribution of the time-dependent variables
    public boolean[] getInfectionDistribution(){
      Bag agents = friends.getAllNodes();
      boolean[] distribution = new boolean[agents.numObjs];
      for(int i = 0; i < agents.numObjs; i++){
          distribution[i] = ((Patient)(agents.objs[i])).getInfected();
      }
      return distribution;
    }
    public boolean[] getVaccineDistribution(){
        Bag agents = friends.getAllNodes();
        boolean[] distribution = new boolean[agents.numObjs];
        for(int i = 0; i < agents.numObjs; i++){
            distribution[i] = ((Patient)(agents.objs[i])).getTreatment();
        }
        return distribution;
    }
    public double[] getCumulativeDistanceDistribution(){
        Bag agents = friends.getAllNodes();
        double[] distribution = new double[agents.numObjs];
        for(int i = 0; i < agents.numObjs; i++){
            distribution[i] = ((Patient)(agents.objs[i])).getCumulativeDistance();
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
        friends.clear();

        // ADD PATIENTS TO THE YARD
        for (int i = 0; i < this.getNumPatients(); i++){
            Patient patient = new Patient("Patient_"+i, this);
            yard.setObjectLocation(patient, new Double2D(yard.getWidth() * 0.5 +
                    initialLocationMultiplier * random.nextDouble() -
                    initialLocationMultiplier * 0.5,
                    yard.getHeight() * 0.5 +
                            initialLocationMultiplier * random.nextDouble() -
                            initialLocationMultiplier * 0.5));
            friends.addNode(patient);
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
            fileParams.addProbTreatment(Double.parseDouble(columns[1]));
            fileParams.addTransmissionEffect(Integer.parseInt(columns[2]));
            fileParams.addSexOverInfection(Integer.parseInt(columns[3]));
            fileParams.addSexOverVaccine(Integer.parseInt(columns[4]));
            fileParams.addVaccineOverInfection(Integer.parseInt(columns[5]));
            fileParams.addPromiscuityPopulation(Double.parseDouble(columns[6]));
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
                ((City) state).setProbVaccine(fileParams.getProbTreatment(i));
                ((City) state).setTransmissionEffect(fileParams.getTransmissionEffect(i));
                ((City) state).setSexOverInfection(fileParams.getSexOverInfection(i));
                ((City) state).setSexOverVaccine(fileParams.getSexOverVaccine(i));
                ((City) state).setVaccineOverInfection(fileParams.getVaccineOverInfection(i));
                ((City) state).setPromiscuityPopulation(fileParams.getPromiscuityPopulation(i));

                // RUN THE SIM
                state.start();
                System.out.println("Executing SIM "+ i + "...");
                do {
                    if (!state.schedule.step(state)) break;
                }
                while(state.schedule.getSteps() < ((City) state).earlyStop);
                state.finish();
            }

            System.exit(0);

        } else {
            System.out.println("uniSIM MODE");
            state.start();
            do {
                if (!state.schedule.step(state)) break;
            }
            while(state.schedule.getSteps() < ((City) state).earlyStop);
            state.finish();
            System.exit(0);
        }
    }
}