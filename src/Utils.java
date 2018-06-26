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

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;

/** UTILS
 * This class will contain the functions to generate the data for the micro-simulation
 */
public class Utils implements Steppable {

    private String dirName = "output";

    /** UTILS CONSTRUCTOR
     * Every time the object is called it will create the file to save the changes in the simulation
     */

    public Utils(City city) throws Exception {
        createDataFile(city);
    }

    /** Create a datafile for the evolution of the SIM
     */

    private void createDataFile(City city) throws Exception {
        Writer writer = null;

        File dir = new File(dirName);
        if (! dir.exists()){
            dir.mkdir();
        }

        try {
            writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(dirName + "/" + city.getFileNameHash(), false),
                    "utf-8"));

            writer.write("numPatients=" + city.getNumPatients() + "," +
                            "probInfected=" + city.getProbInfected() + ","  +
                            "probVaccine=" + city.getProbVaccine() + ","  +
                            "lambda=" + city.getLambda() + "," +
                            "contagion=" + city.getContagion() + ","  +
                            "infectiousness=" + city.getInfectiousness() + ","  +
                            "sexOnInfection=" + city.getSexOnInfection() + ","  +
                            "sexOnVaccine=" + city.getSexOnVaccine() + ","  +
                            "vaccineOnInfection=" + city.getVaccineOnInfection() + ","  +
                            "promiscuityPopulation=" + city.getPromiscuityPopulation() + ","  +
                            "maxPartnerForce=" + city.getMaxPartnerForce() + "," +
                            "randomForce=" + city.getRandomForce() + "," +
                            "partnerForce=" + city.getPartnerForce()
            );
            writer.write(System.getProperty("line.separator"));
            writer.write("step,agent,sex,vaccine,infection,degree,contagionDist,infectiousnessDist,indInterference");
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

    public void writeStep(City city) throws Exception {

        long step = city.schedule.getSteps();
        Writer writer = null;
        Patient patient;

        try {
            writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(dirName + "/" + city.getFileNameHash(), true),
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
                        "," + patient.getDegree() +
                        "," + patient.getContagionDistance() +
                        "," + patient.getInfectiousnessDistance() +
                        "," + patient.getIndirectInterference());

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

    // Method to encrypt the filename: blowfish method:
    // src = http://www.adeveloperdiary.com/java/how-to-easily-encrypt-and-decrypt-text-in-java/
    public static String encrypt(String strClearText, String strKey) throws Exception{
        String strData="";

        try {
            SecretKeySpec skeyspec = new SecretKeySpec(strKey.getBytes(),"Blowfish");
            Cipher cipher=Cipher.getInstance("Blowfish");
            cipher.init(Cipher.ENCRYPT_MODE, skeyspec);
            byte[] encrypted=cipher.doFinal(strClearText.getBytes());
            strData=new String(encrypted);

        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e);
        }
        return strData;
    }
    /** STEP UTILS
     * The micro-simulation will be saved to a .txt file though the course of the simulation
     * @param state of the simulation
     */

    public void step(SimState state){
        City city = (City) state;
        try {
            writeStep(city);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
