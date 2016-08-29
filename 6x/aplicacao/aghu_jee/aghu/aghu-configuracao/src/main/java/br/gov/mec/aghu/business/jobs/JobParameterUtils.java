package br.gov.mec.aghu.business.jobs;

import java.util.StringTokenizer;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;

public class JobParameterUtils {

	public static RapServidoresId stringToServidorId(String strServidorId) {
		if (StringUtils.isBlank(strServidorId)) {
			return null;
		}
		
		StringTokenizer tokens = new StringTokenizer(strServidorId, "|");
		Integer matricula = Integer.valueOf(tokens.nextToken());
		Short vinCodigo = Short.valueOf(tokens.nextToken());
		
		RapServidoresId servidorId = new RapServidoresId();
		servidorId.setMatricula(matricula);
		servidorId.setVinCodigo(vinCodigo);
		
		return servidorId;
	}
	
	public static String servidorIdToString(RapServidores servidor) {
		if (servidor == null || servidor.getId() == null
				|| servidor.getId().getMatricula() == null
				|| servidor.getId().getVinCodigo() == null) {
			return null;
		}
		
		return (servidor.getId().getMatricula() + "|" + servidor.getId().getVinCodigo());
	}

}
