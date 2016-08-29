package br.gov.mec.aghu.internacao.business;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.business.seguranca.Secure;

@Stateless
public class RelatorioLocalizadorPacienteON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(RelatorioLocalizadorPacienteON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IAmbulatorioFacade ambulatorioFacade;

@EJB
private IAghuFacade aghuFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = 3669125760412420615L;

	public Map<String, Object> getParematros(Integer codPaciente) {

		List<Object[]> res = this.getAghuFacade().pesquisarDadosPacienteAtendimento(codPaciente);

		Map<String, Object> params = new HashMap<String, Object>();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		Iterator<Object[]> it = res.iterator();
		
		if (it.hasNext()) {
			Object[] item = it.next();
			if(item[0] != null){
				params.put("NOME", this.abreviarNome((String)item[0]));
			}
			
			if(item[1] != null){
				params.put("DESCRICAO", this.getDescricaoLocal((Short)item[1]));
			}
			
			if(item[2] != null){
				params.put("DATA", sdf.format((Date)item[2]));
			}
						
		}
		
		params.put("nomeRelatorio", "mamr_localiza_emg");
		
		
		return params;

	}

	public String abreviarNome(String nomeCompleto) {
		/**
		 * Esta funcao retorna o nome e sobrenome do paciente e os nomes
		 * intermediários com a primeira letra e ponto.
		 */
		String retorno;
		// String nomeCompleto = "MARIA MARGARIDA RIBEIRO DA COSTA DOS SANTOS";
		String[] nomePalavras = StringUtils.split(nomeCompleto, " ");
		StringBuffer palavra = new StringBuffer();
		int i;
		for (i = 1; i < nomePalavras.length - 1; i++) {
			palavra.append(StringUtils.substring(nomePalavras[i], 0, 1))
					.append(". ");
		}
		if (nomePalavras.length - 1 > 0) {
			retorno = nomePalavras[0] + " " + palavra
					+ nomePalavras[nomePalavras.length - 1];
		} else {
			retorno = nomePalavras[0] + " " + palavra;
		}

		return retorno;
	}

	@Secure("#{s:hasPermission('relatorio','localizadorPaciente')}")
	public String getDescricaoLocal(Short seq){

		String descricao = getAmbulatorioFacade().getPrimeiraDescricaoLocal(seq);
		
		return descricao.toUpperCase();
	}
	
	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}
	
	protected IAmbulatorioFacade getAmbulatorioFacade() {
		return this.ambulatorioFacade;
	}
}
