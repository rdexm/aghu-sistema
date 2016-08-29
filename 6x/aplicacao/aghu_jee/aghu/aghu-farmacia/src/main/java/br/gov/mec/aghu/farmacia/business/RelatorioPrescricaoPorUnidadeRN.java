package br.gov.mec.aghu.farmacia.business;

import java.io.Serializable;
import java.util.Date;

import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacaoPrescricao;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MpmPrescricaoMedica;
import br.gov.mec.aghu.core.business.BaseBusiness;


@Stateless
public class RelatorioPrescricaoPorUnidadeRN extends BaseBusiness implements Serializable{

private static final Log LOG = LogFactory.getLog(RelatorioPrescricaoPorUnidadeRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7654676357682994143L;

	public enum SituacoesPrescricoesRelatorio {
		PRESCRICAO_COM_ALTA_MEDICA("A"), 
		PRESCRICAO_EM_USO("U"), 
		PRESCRICAO_PENDENTE("*"),
		NULL("");

		@SuppressWarnings("PMD.AtributoEmSeamContextManager")
		private String fields;

		private SituacoesPrescricoesRelatorio(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}
	
	/**
	 * Identifica o nome de cada chave declarada nos arquivos properties para o
	 * nome do local de atendimento
	 */
	public static enum LocalAtendimento {
		RELATORIO_LOCAL_ATENDIMENTO_LEITO, RELATORIO_LOCAL_ATENDIMENTO_QUARTO, RELATORIO_LOCAL_ATENDIMENTO_UNIDADE_FUNCIONAL;
	}
	
	/**
	 * @ORADB afac_sit_conf_prcr
	 * @param atendimento
	 * @param datareferencia
	 * @return
	 */
	public String obterSituacaoPrescricao(AghAtendimentos aghAtendimentos, Date dataReferencia, MpmPrescricaoMedica prescricao){
		if(aghAtendimentos.getSumariosAlta() !=null && !aghAtendimentos.getSumariosAlta().isEmpty()){
			return SituacoesPrescricoesRelatorio.PRESCRICAO_COM_ALTA_MEDICA.toString();
		}else if(prescricao == null){
			return SituacoesPrescricoesRelatorio.NULL.toString();
		}else{
			if(DominioSituacaoPrescricao.U.equals(prescricao.getSituacao())) {
				return SituacoesPrescricoesRelatorio.PRESCRICAO_EM_USO.toString();
			} else if(prescricao.getServidorValida()== null || (prescricao.getServidorValida()!= null && prescricao.getServidorValida().getId().getMatricula() == null)) {
				return SituacoesPrescricoesRelatorio.PRESCRICAO_PENDENTE.toString();
			} else {
				return SituacoesPrescricoesRelatorio.NULL.toString();
			}
		}
		
	}
	
	/**
	 * @ORADB mpmc_localiza_pac
	 * @param leito
	 * @param quarto
	 * @param unidadeFuncional
	 * @return
	 *  
	 */
	public String obterLocalizacaoPacienteParaRelatorio(AghAtendimentos aghAtendimentos){
		String local = null;
		
		if(aghAtendimentos == null) {
			throw new IllegalArgumentException("Parâmetro Inválido!");
		}
		
		if (aghAtendimentos.getLeito() != null) {
			local = buscarMensagemLocalizada(
					LocalAtendimento.RELATORIO_LOCAL_ATENDIMENTO_LEITO.toString(),
					aghAtendimentos.getLeito().getLeitoID());
		} else if (aghAtendimentos.getQuarto() != null) {
			local = buscarMensagemLocalizada(
					LocalAtendimento.RELATORIO_LOCAL_ATENDIMENTO_QUARTO.toString(),
					aghAtendimentos.getQuarto().getDescricao());
		} else if (aghAtendimentos.getUnidadeFuncional() != null) {
			AghUnidadesFuncionais unidadeFuncional = aghAtendimentos
					.getUnidadeFuncional();
			if(unidadeFuncional.getSigla() != null){
				local = buscarMensagemLocalizada(
						LocalAtendimento.RELATORIO_LOCAL_ATENDIMENTO_UNIDADE_FUNCIONAL
								.toString(), unidadeFuncional
								.getSigla());
			}else if(unidadeFuncional.getLPADAndarAla() !=null){
				local = buscarMensagemLocalizada(
						LocalAtendimento.RELATORIO_LOCAL_ATENDIMENTO_UNIDADE_FUNCIONAL
								.toString(), unidadeFuncional
								.getLPADAndarAla());
			}
		} 
		return local;
		
	}
	
	public String buscarMensagemLocalizada(String chave, Object... parametros) {
		String mensagem = getResourceBundleValue(chave);
		mensagem = java.text.MessageFormat.format(mensagem, parametros);

		return mensagem;
	}
	
}
