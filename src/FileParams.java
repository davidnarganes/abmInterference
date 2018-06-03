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

import java.util.ArrayList;

/** FILEPARAMS CLASS
The columns of the `params` file will be stored as properties of a FileParams object
 */

public class FileParams {
    private ArrayList<Double> probInfected;
    private ArrayList<Double> probTreatment;
    private ArrayList<Integer> transmissionEffect;
    private ArrayList<Integer> sexOverInfection;
    private ArrayList<Integer> sexOverVaccine;
    private ArrayList<Integer> vaccineOverInfection;
    private ArrayList<Double> promiscuityPopulation;

    // CONSTRUCTOR OF FILEPARAMS
    public FileParams(){
        this.probInfected = new ArrayList<>();
        this.probTreatment = new ArrayList<>();
        this.transmissionEffect = new ArrayList<>();
        this.sexOverInfection  = new ArrayList<>();
        this.sexOverVaccine  = new ArrayList<>();
        this.vaccineOverInfection = new ArrayList<>();
        this.promiscuityPopulation  = new ArrayList<>();
    }

    // DEFINE GETTERS AND SETTERS FOR THE PROPERTIES OF FILEPARAMS
    public double getProbInfected(int index){
        return this.probInfected.get(index);
    }
    public void addProbInfected(Double value){
        this.probInfected.add(value);
    }
    public double getProbTreatment(int index){
        return this.probInfected.get(index);
    }
    public void addProbTreatment(Double value){
        this.probTreatment.add(value);
    }
    public int getTransmissionEffect(int index){
        return this.transmissionEffect.get(index);
    }
    public void addTransmissionEffect(Integer value){
        this.transmissionEffect.add(value);
    }
    public int getSexOverInfection(int index){
        return this.sexOverInfection.get(index);
    }
    public void addSexOverInfection(Integer value){
        this.sexOverInfection.add(value);
    }
    public int getSexOverVaccine(int index){
        return this.transmissionEffect.get(index);
    }
    public void addSexOverVaccine(int value){
        this.transmissionEffect.add(value);
    }
    public int getVaccineOverInfection(int index){
        return this.vaccineOverInfection.get(index);
    }
    public void addVaccineOverInfection(int value){
        this.vaccineOverInfection.add(value);
    }
    public double getPromiscuityPopulation(int index){
        return this.promiscuityPopulation.get(index);
    }
    public void addPromiscuityPopulation(double value){
        this.promiscuityPopulation.add(value);
    }
}
