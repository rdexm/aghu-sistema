package br.gov.mec.aghu.casca.business;

import java.io.Serializable;

import javax.ejb.Local;

import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * Componente registrado em <code>components.xml</code>: 
 * <code>centralPendencias</code>
 * @author gandriotti
 *
 */
@Local
public interface CentralPendenciasInterface extends Serializable {

	/**
	 * A ordem de prioridades corresponde a ordem em que as mesmas se encontram, isto eh,
	 * quanto <b>MENOR</b> {@link TipoPendenciaEnum#ordinal()} <b>MAIOR</b> a prioridade. 
	 * @author gandriotti
	 *
	 */	
	
	
	public static enum TipoAcaoEnum {
		LINK,
		LINK_E_EXCLUSAO,
		INFORMACAO,
		METODO,
		SEM_ACAO;

	}

	
	public static enum TipoPendenciaEnum {
		BLOQUEANTE("red"), // exige intervencao do usuario (modal com botao de redirecinamento)
		INTRUSIVA("yellow"), // apresenta uma modal a cada interacao solicitando intervencao
		LEMBRETE (""), // abre um "slider" a cada interacao, mas fecha automaticamente
		IRRELEVANTE(""), // sem qualquer interferencia
		INEXISTENTE(""); // sem interferencia e sem pendencias
		
		private String htmlColor;
		
		private TipoPendenciaEnum(String htmlColor) {
			this.htmlColor=htmlColor;
		}
		
		public String getHtmlColor(){
			return this.htmlColor;
		}

	}

	public TipoPendenciaEnum getTipoPendenciaMaiorPrioridade()
			throws ApplicationBusinessException;

	public String getUrlCentralPendenciasMaiorPrioridade()
			throws ApplicationBusinessException;

	public String getMensagemAvisoMaiorPrioridade()
			throws ApplicationBusinessException;
}
