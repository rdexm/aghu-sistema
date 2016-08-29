package br.gov.mec.aghu.exameselaudos.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSismamaMamoCadCodigo;
import br.gov.mec.aghu.core.business.BaseBusiness;

/**
 * Classe criada para separar métodos da LaudoMamografiaRN, de forma a evitar o problema de PMD tooManyMethods
 * @author bruno.mourao
 *
 */
@Stateless
public class LaudoMamografiaObtencaoDadosRN extends BaseBusiness{


private static final String DEFAULT = "Default";

@EJB
private LaudoMamografiaRN laudoMamografiaRN;

private static final Log LOG = LogFactory.getLog(LaudoMamografiaObtencaoDadosRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2733608063704193263L;
	private static final String STRING_BR = "\n<br/>";

	public String obterDadosInformacoesClinicas(Integer iseSoeSeq, Short iseSeqp) {
		String resposta;
		
		
		resposta = laudoMamografiaRN.obterRespostaMamo(iseSoeSeq, iseSeqp, DominioSismamaMamoCadCodigo.C_INF_CLINICA);
		
		if (StringUtils.isNotBlank(resposta)) {
			return "--------- INFORMAÇÕES CLÍNICAS ----------".concat(STRING_BR).concat(resposta).concat(STRING_BR);
		}
		//linha em branco
		return STRING_BR;
	}
	
	public String obterDadosObservacao(Integer iseSoeSeq, Short iseSeqp) {
		String resposta = laudoMamografiaRN.obterRespostaMamo(iseSoeSeq, iseSeqp, DominioSismamaMamoCadCodigo.C_OBS_GERAIS);
		if (StringUtils.isNotBlank(resposta)) {
			return STRING_BR.concat("--------- OBSERVAÇÕES ----------").concat(STRING_BR).concat(resposta).concat(STRING_BR);
		}
		return "";
	}
	
	public String obterDadosResidente(Integer iseSoeSeq, Short iseSeqp) {
		String resposta = laudoMamografiaRN.obterRespostaMamo(iseSoeSeq, iseSeqp, DominioSismamaMamoCadCodigo.C_RESIDENTE);
		if (StringUtils.isNotBlank(resposta)) {
			return STRING_BR.concat("Residente: ").concat(resposta).concat(STRING_BR);
		}
		return "";
	}

	public String obterDadosMama(Integer iseSoeSeq, Short iseSeqp, DominioSismamaMamoCadCodigo numFilm, DominioSismamaMamoCadCodigo pele, DominioSismamaMamoCadCodigo composicao, String complemento) {
	
		String resposta = laudoMamografiaRN.obterRespostaMamo(iseSoeSeq, iseSeqp, numFilm);
		StringBuilder sb = new StringBuilder();
		if (StringUtils.isNotBlank(resposta)) {
			sb.append(STRING_BR).append("MAMA ").append(complemento).append(STRING_BR);
			resposta = laudoMamografiaRN.obterDescricaoRespostaMamo(iseSoeSeq, iseSeqp, pele, "C");
			if (StringUtils.isNotBlank(resposta)) {
				sb.append("Pele ").append(resposta).append(STRING_BR);
			}
			resposta = laudoMamografiaRN.obterDescricaoRespostaMamo(iseSoeSeq, iseSeqp, composicao, "C");
			if (StringUtils.isNotBlank(resposta)) {
				sb.append(resposta).append(STRING_BR);
			}
		}
		return sb.toString();
	}
	
	public Boolean obterDadosAchadosMama(StringBuilder laudo, String complemento, DominioSismamaMamoCadCodigo campo, Boolean nodulo, Integer iseSoeSeq, Short iseSeqp, Boolean quebraLinha) {

		String resposta = laudoMamografiaRN.obterRespostaMamo(iseSoeSeq, iseSeqp, campo);
		StringBuilder retorno = new StringBuilder();
		if (StringUtils.equals(resposta, "3")) {
			if (!nodulo) {
				retorno.append("Nódulo: ");
				nodulo = Boolean.TRUE;
			}
			retorno.append(complemento);
			if (quebraLinha) {
				retorno.append(STRING_BR);
			}
		}
		laudo.append(retorno.toString());
		
		return nodulo;
	}

	public String obterDadosNodulo(DominioSismamaMamoCadCodigo nodulo, DominioSismamaMamoCadCodigo localizacao, DominioSismamaMamoCadCodigo tamanho, DominioSismamaMamoCadCodigo contorno, DominioSismamaMamoCadCodigo limite, Integer iseSoeSeq, Short iseSeqp) {
	
		String resposta = laudoMamografiaRN.obterRespostaMamo(iseSoeSeq, iseSeqp, nodulo);
		StringBuilder montaResp = new StringBuilder();
		String retorno = "";
		if (StringUtils.equals(resposta, "3")) {
			resposta = laudoMamografiaRN.obterDescricaoRespostaMamo(iseSoeSeq, iseSeqp, localizacao, "C");
			if (!StringUtils.equals(resposta != null ? resposta : DEFAULT, DEFAULT)) {
				montaResp.append("Nódulo - Localização: ").append(resposta);
				resposta = laudoMamografiaRN.obterDescricaoRespostaMamo(iseSoeSeq, iseSeqp, tamanho, "C");
				montaResp.append(" - Tamanho: ").append(resposta);
				resposta = laudoMamografiaRN.obterDescricaoRespostaMamo(iseSoeSeq, iseSeqp, contorno, "C");
				montaResp.append(" - Contorno: ").append(resposta);
				resposta = laudoMamografiaRN.obterDescricaoRespostaMamo(iseSoeSeq, iseSeqp, limite, "C");
				montaResp.append(" - Limite: ").append(resposta).append(STRING_BR);
				retorno = montaResp.toString();
			}
		}
		return retorno;
	}
	
	public String obterDadosMicroCalficicacao(DominioSismamaMamoCadCodigo localizacao, DominioSismamaMamoCadCodigo forma, DominioSismamaMamoCadCodigo distribuicao, Integer iseSoeSeq, Short iseSeqp) {
	
		String resposta = laudoMamografiaRN.obterDescricaoRespostaMamo(iseSoeSeq, iseSeqp, localizacao, "C");
		StringBuilder montaResp = new StringBuilder();
		String retorno = "";
		if (!StringUtils.equals(resposta != null ? resposta : DEFAULT, DEFAULT)) {
			montaResp.append("Microcalcificação - Localização: ").append(resposta);
			resposta = laudoMamografiaRN.obterDescricaoRespostaMamo(iseSoeSeq, iseSeqp, forma, "C");
			montaResp.append(" - Forma: ").append(resposta);
			resposta = laudoMamografiaRN.obterDescricaoRespostaMamo(iseSoeSeq, iseSeqp, distribuicao, "C");
			montaResp.append(" - Distribuição: ").append(resposta).append(STRING_BR);
			retorno = montaResp.toString();
		}
		return retorno;
	}
	
	public String obterDadosAssimetriaDistorcaoAreaDensa(String label, DominioSismamaMamoCadCodigo campo, Integer iseSoeSeq, Short iseSeqp) {
	
		String resposta = laudoMamografiaRN.obterDescricaoRespostaMamo(iseSoeSeq, iseSeqp, campo, "C");
		if (!StringUtils.equals(resposta != null ? resposta : DEFAULT, DEFAULT)) {
			return label.concat(resposta).concat(STRING_BR);
		}
		return "";
	}
	
	public Boolean obterDadosLinfonodos(StringBuilder laudo, String complemento, Boolean linfonodos, DominioSismamaMamoCadCodigo campo, Integer iseSoeSeq, Short iseSeqp) {
	
		String resposta = "";
		String retorno = "";
		if (StringUtils.isNotBlank(complemento)) {
			resposta = laudoMamografiaRN.obterRespostaMamo(iseSoeSeq, iseSeqp, campo);
			if (StringUtils.equals(resposta, "3")) {
				if (!linfonodos) {
					retorno = "Linfonodos Axilares";
					linfonodos = Boolean.TRUE;
				}
				retorno = retorno.concat(StringUtils.isNotBlank(complemento) ? complemento : resposta);
			}
		} else {
			resposta = laudoMamografiaRN.obterDescricaoRespostaMamo(iseSoeSeq, iseSeqp, campo, "C");
			if (StringUtils.isNotBlank(resposta)) {
				if (!linfonodos) {
					retorno = "Linfonodos Axilares";
					linfonodos = Boolean.TRUE;
				}
				retorno = retorno.concat(StringUtils.isNotBlank(complemento) ? complemento : " - " + resposta);
			}
		}
		if (StringUtils.isNotBlank(retorno)) {
			laudo.append(retorno);
		}
		return linfonodos;
	}
}
