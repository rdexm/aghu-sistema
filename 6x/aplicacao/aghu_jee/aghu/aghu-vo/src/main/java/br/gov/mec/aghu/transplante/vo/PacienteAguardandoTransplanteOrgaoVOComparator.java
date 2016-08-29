

package br.gov.mec.aghu.transplante.vo;
import java.util.Comparator;
import java.util.Date;

public class PacienteAguardandoTransplanteOrgaoVOComparator{
	
	public static class OrderByDataRegistroInativado implements Comparator<PacienteAguardandoTransplanteOrgaoVO> {

        @Override
        public int compare(PacienteAguardandoTransplanteOrgaoVO o1, PacienteAguardandoTransplanteOrgaoVO o2) {
             Date date1 = o1.getDataRegistroInativado();
             Date date2 = o2.getDataRegistroInativado();  
             return date1.compareTo(date2);
        }
    }
	
}
