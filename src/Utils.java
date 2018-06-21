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
import sim.util.Bag;
import java.io.*;

/** UTILS
 * This class will contain the functions to generate the data for the micro-simulation
 */
public class Utils implements Steppable {

    private String dirName = "SIM_output";

    /** UTILS CONSTRUCTOR
     * Every time the object is called it will create the file to save the changes in the simulation
     */

    public Utils(City city){
        createDataFile(city);
    }

    /** CREATE DATAFILE FOR MICRO-SIMULATION
     * The columns of the data frame will be created at this point:
     * 1. Step
     * 2. Agent name
     * 3. Sex
     * 4. Treatment
     * 5. Number of infected patients in the population
     * 6. Cumulative infected distance
     */

    private void createDataFile(City city){
        Writer writer = null;

        String filename = (city.getProbInfected() + "_" +
                city.getProbVaccine() + "_" +
                city.getContagion() + "_" +
                city.getInfectiousness() + "_" +
                city.getSexOnInfection() + "_" +
                city.getSexOnVaccine() + "_" +
                city.getVaccineOnInfection() + "_" +
                city.getPromiscuityPopulation() + "_" +
                city.getNumPatients() +
                ".txt");

        File dir = new File(dirName);
        if (! dir.exists()){
            dir.mkdir();
        }

        try {
            writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(dirName + "/" + filename, false),
                    "utf-8"));
            writer.write("probInfected:" + city.getProbInfected() + ","  +
                    "probVaccine:" + city.getProbVaccine() + ","  +
                    "contagion:" + city.getContagion() + ","  +
                    "sexOnInfection:" + city.getSexOnInfection() + ","  +
                    "sexOnVaccine:" + city.getSexOnVaccine() + ","  +
                    "vaccineOnInfection:" + city.getVaccineOnInfection() + ","  +
                    "promiscuityPopulation:" + city.getPromiscuityPopulation() + ","  +
                    "numPatients:" + city.getNumPatients()

            );
            writer.write(System.getProperty("line.separator"));
            writer.write("step,agent,sex,treatment,outcome,count_infected,cumulative_distance");
            writer.write(System.getProperty("line.separator"));
        } catch (IOException ex){
            System.out.println("Error creating file");
        } finally {
            try {
                writer.close();
            } catch (Exception ex){
                System.out.println("Error closing file");
            }
        }
    }

    /** WRITE EACH STEP
     * The above defined parameters will be saved at each interval of the simulation
     * @param city to import the state of the simulation
     */

    public void writeStep(City city){

        long step = city.schedule.getSteps();
        Writer writer = null;

        String filename = (city.getProbInfected() + "_" +
                city.getProbVaccine() + "_" +
                city.getContagion() + "_" +
                city.getInfectiousness() + "_" +
                city.getSexOnInfection() + "_" +
                city.getSexOnVaccine() + "_" +
                city.getVaccineOnInfection() + "_" +
                city.getPromiscuityPopulation() + "_" +
                city.getNumPatients() +
                ".txt");

        Patient patient;

        try {
            writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(dirName + "/" + filename, true),
                    "utf-8")
            );

            Bag agents = city.yard.getAllObjects();

            for (int i = 0; i < agents.size(); i++){
                patient = (Patient) agents.get(i);
                writer.write(step +
                        "," + patient.getName() +
                        "," + patient.getSex() +
                        "," + patient.getVaccine() +
                        "," + patient.getInfected() +
                        "," + patient.count_infected(city) +
                        "," + patient.getIndirectInferference());
                writer.write(System.getProperty("line.separator"));
            }
        } catch (IOException ex){
            System.out.println("Error writing file");
        } finally {
            try {
                writer.close();
            } catch (Exception ex){
                System.out.println("Error closing file");
            }
        }
    }

    /** STEP UTILS
     * The micro-simulation will be saved to a .txt file though the course of the simulation
     * @param state of hte SIMulation
     */

    public void step(SimState state){
        City city = (City) state;
        writeStep(city);
    }
}
