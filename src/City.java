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
    private double probInfected = 0.001;
    private double probVaccine = 0.001;
    private double lambda = 0.3;
    private double contagion = 3.0;
    private double infectiousness = 3.0;
    private double sexOnVaccine = 3.0;
    private double sexOnInfection = 3.0;
    private double vaccineOnInfection = 3.0;
    private double promiscuityPopulation = 0.005;
    private double maxPartnerForce = 5.0;
    private double randomForce = 5.0;
    private double partnerForce = 3.0;
    private String filenameHash;

    // Other variables
    private Scanner inputStream;
    private int lines = 0;
    private boolean multiSIM = true;
    public int earlyGUIStop = 300;
    private int numIntervals = 1;
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
        this.setInfectiousness(infectiousness);
        this.setSexOnVaccine(sexOnVaccine);
        this.setSexOnInfection(sexOnInfection);
        this.setVaccineOnInfection(vaccineOnInfection);
        this.setPromiscuityPopulation(promiscuityPopulation);
        this.calculateFilenameHash();
    }

    public void calculateFilenameHash(){
        // Define a unique hash for each file based on currentTimeMillis
        String filename = (System.currentTimeMillis() + "time");

        Integer filenameHash = filename.hashCode();
        this.filenameHash = filenameHash.toString() + ".csv";
    }

    // Getters
    public int getNumPatients(){return numPatients;}
    public double getProbInfected(){return probInfected;}
    public double getProbVaccine(){return probVaccine;}
    public double getLambda(){return lambda;}
    public double getContagion(){return contagion;}
    public double getInfectiousness(){return infectiousness;}
    public double getSexOnVaccine(){return sexOnVaccine;}
    public double getSexOnInfection(){return sexOnInfection;}
    public double getVaccineOnInfection(){return vaccineOnInfection;}
    public double getPromiscuityPopulation(){return promiscuityPopulation;}
    public double getMaxPartnerForce(){return maxPartnerForce;}
    public double getRandomForce(){return randomForce;}
    public double getPartnerForce(){return partnerForce;}
    public String getFileNameHash(){return filenameHash.toString();}
    private int getLines(){return lines;}

    // Setters
    public void setNumPatients(int value){numPatients = value;}
    public void setProbInfected(double value){probInfected = value;}
    public void setProbVaccine(double value){probVaccine = value;}
    public void setLambda(double value){lambda = value;}
    public void setContagion(double value){contagion = value;}
    public void setInfectiousness(double value){infectiousness = value;}
    public void setSexOnVaccine(double value){sexOnVaccine = value;}
    public void setSexOnInfection(double value){sexOnInfection = value;}
    public void setVaccineOnInfection(double value){vaccineOnInfection = value;}
    public void setPromiscuityPopulation(double value){promiscuityPopulation = value;}
    public void setMaxPartnerForce(double value){maxPartnerForce = value;}
    public void setRandomForce(double value){randomForce = value;}
    public void setPartnerForce(double value){partnerForce = value;}
    public void setFilenameHash(){calculateFilenameHash();}
    private void setLines(int value){lines = value;}

    // Domains: slider in the GUI controler
    public Object domNumPatients(){return new Interval(1,3000);}
    public Object domProbInfected(){return new Interval(0.0,0.5);}
    public Object domProbVaccine(){return new Interval(0.0,0.5);}
    public Object domLambda(){return new Interval(0.0,50.0);}
    public Object domContagion(){return new Interval(0.0,20);}
    public Object domInfectiousness(){return new Interval(0.0,20);}
    public Object domSexOnVaccine(){return new Interval(1.0,20.0);}
    public Object domSexOnInfection(){return new Interval(1.0,20.0);}
    public Object domVaccineOnInfection(){return new Interval(1.0,20.0);}
    public Object domPromiscuityPopulation(){return new Interval(0.0,1.0);}
    public Object domMaxPartnerForce(){return new Interval(0.0,this.getPartnerForce() * 1.5);}
    public Object domRandomForce(){return new Interval(0.0,20.0);}
    public Object domPartnerForce(){return new Interval(0.0,20.0);}


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
        Utils utils = null;
        try {
            utils = new Utils(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
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

            int i = 0;
            fileParams.addNumPatients(Integer.parseInt(columns[i++]));
            fileParams.addProbInfected(Double.parseDouble(columns[i++]));
            fileParams.addProbVaccine(Double.parseDouble(columns[i++]));
            fileParams.addLambda(Double.parseDouble(columns[i++]));
            fileParams.addContagion(Double.parseDouble(columns[i++]));
            fileParams.addInfectiousness(Double.parseDouble(columns[i++]));
            fileParams.addSexOnInfection(Double.parseDouble(columns[i++]));
            fileParams.addSexOnVaccine(Double.parseDouble(columns[i++]));
            fileParams.addVaccineOnInfection(Double.parseDouble(columns[i++]));
            fileParams.addPromiscuityPopulation(Double.parseDouble(columns[i++]));
            fileParams.addMaxPartnerForce(Double.parseDouble(columns[i++]));
            fileParams.addRandomForce(Double.parseDouble(columns[i++]));
            fileParams.addPartnerForce(Double.parseDouble(columns[i++]));
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
        state = new City(System.currentTimeMillis());
        multiSIM = ((City) state).multiSIM;

        if (multiSIM){

            System.out.println("multiSIM MODE");
            FileParams fileParams;
            fileParams = ((City) state).readFile();

            for (int i = 0; i < ((City) state).getLines(); i++){
                System.out.println("Executing SIM "+ i + "...");

                // Define params of SIM at each iteration:
                // 1. Get ALL params from fileParams
                // 2. Set ALL params for current SIM

                ((City) state).setNumPatients(fileParams.getNumPatients(i));
                ((City) state).setProbInfected(fileParams.getProbInfected(i));
                ((City) state).setProbVaccine(fileParams.getProbVaccine(i));
                ((City) state).setLambda(fileParams.getLambda(i));
                ((City) state).setContagion(fileParams.getContagion(i));
                ((City) state).setInfectiousness(fileParams.getInfectiousness(i));
                ((City) state).setSexOnInfection(fileParams.getSexOnInfection(i));
                ((City) state).setSexOnVaccine(fileParams.getSexOnVaccine(i));
                ((City) state).setVaccineOnInfection(fileParams.getVaccineOnInfection(i));
                ((City) state).setPromiscuityPopulation(fileParams.getPromiscuityPopulation(i));
                ((City) state).setMaxPartnerForce(fileParams.getMaxPartnerForce(i));
                ((City) state).setRandomForce(fileParams.getRandomForce(i));
                ((City) state).setPartnerForce(fileParams.getPartnerForce(i));
                ((City) state).setFilenameHash();

                System.out.println("sexOnVaccine = " + ((City) state).getSexOnVaccine());

                // RUN THE SIM
                state.start();
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