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
    private double probVaccine = 0.0002;
    private double probEdge = 0.03;
    private double lambda = 0.3;
    private double contagion = 0.5;
    private double infectiousness = 0.5;
    private int sexOnVaccine = 3;
    private int sexOnInfection = 3;
    private int vaccineOnInfection = 3;
    private double promiscuityPopulation = 0.01;
    private double maxPartnerForce = 5.0;
    private double forceCenter = 0.0;
    private double randomMultiplier = 5.0;
    private double partnerMultiplier = 3.0;

    // Other variables
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

    // Getters
    public int getNumPatients(){return numPatients;}
    public double getProbInfected(){return probInfected;}
    public double getProbVaccine(){return probVaccine;}
    public double getProbEdge(){return probEdge;}
    public double getLambda(){return lambda;}
    public double getContagion(){return contagion;}
    public double getInfectiousness(){return infectiousness;}
    public int getSexOnVaccine(){return sexOnVaccine;}
    public int getSexOnInfection(){return sexOnInfection;}
    public int getVaccineOnInfection(){return vaccineOnInfection;}
    public double getPromiscuityPopulation(){return promiscuityPopulation;}
    public double getMaxPartnerForce(){return maxPartnerForce;}
    public double getForceCenter(){return forceCenter;}
    public double getRandomMultiplier(){return randomMultiplier;}
    public double getPartnerMultiplier(){return partnerMultiplier;}
    public int getLines(){return lines;}

    // Setters
    public void setNumPatients(int value){numPatients = value;}
    public void setProbInfected(double value){probInfected = value;}
    public void setProbVaccine(double value){probVaccine = value;}
    public void setProbEdge(double value){probEdge = value;}
    public void setLambda(double value){lambda = value;}
    public void setContagion(double value){contagion = value;}
    public void setInfectiousness(double value){infectiousness = value;}
    public void setSexOnVaccine(int value){sexOnVaccine = value;}
    public void setSexOnInfection(int value){sexOnInfection = value;}
    public void setVaccineOnInfection(int value){vaccineOnInfection = value;}
    public void setPromiscuityPopulation(double value){promiscuityPopulation = value;}
    public void setMaxPartnerForce(double value){maxPartnerForce = value;}
    public void setForceCenter(double value){forceCenter = value;}
    public void setRandomMultiplier(double value){randomMultiplier = value;}
    public void setPartnerMultiplier(double value){partnerMultiplier = value;}
    public void setLines(int value){lines = value;}

    // Domains
    public Object domNumPatients(){return new Interval(1,3000);}
    public Object domProbInfected(){return new Interval(0.0,0.5);}
    public Object domProbVaccine(){return new Interval(0.0,0.5);}
    public Object domPromEdge(){return new Interval(0.0,1.0);}
    public Object domLambda(){return new Interval(0.0,50.0);}
    public Object domContagion(){return new Interval(0.0,20);}
    public Object domInfectiousness(){return new Interval(0.0,20);}
    public Object domSexOnVaccine(){return new Interval(1,20);}
    public Object domSexOnInfection(){return new Interval(1,20);}
    public Object domVaccineOnInfection(){return new Interval(1,20);}
    public Object domPromiscuityPopulation(){return new Interval(0.0,1.0);}
    public Object domMaxPartnerForce(){return new Interval(0.0,20.0);}
    public Object domForceCenter(){return new Interval(0.0,20.0);}
    public Object domRandomMultiplier(){return new Interval(0.0,20.0);}
    public Object domPartnerMultiplier(){return new Interval(0.0,20.0);}


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
            distribution[i] = ((Patient)(agents.objs[i])).getIndirectInferference();
        }
        return distribution;
    }

    // EDGES DISTRIBUTION
    public int[] getEdgesDegreeDistribution(){
        Bag peers = this.peers.getAllNodes();
        int[] distribution = new int[peers.numObjs];

        for (int i = 0; i < peers.size(); i++){
            distribution[i] = ((Patient) peers.objs[i]).getDegree();
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