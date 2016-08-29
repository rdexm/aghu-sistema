package br.gov.mec.aghu.blococirurgico.business;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.MbcCirurgiasDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;

/**
 * ORADB FUNCTION MBCC_NOME_COM_PONTO
 * 
 * @author aghu
 * 
 */
@Stateless
public class NomeComPontoRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(NomeComPontoRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcCirurgiasDAO mbcCirurgiasDAO;


	private static final long serialVersionUID = 3032531256940794299L;

	/**
	 * ORADB FUNCTION MBCC_NOME_COM_PONTO
	 * <p>
	 * Retorna o nome do paciente com ponto no sobrenome
	 * <p>
	 * 
	 * @param crgSeq
	 */
	public String obterNomeComPonto(Integer crgSeq) {
		String nomePaciente = this.getMbcCirurgiasDAO().obterNomePacienteCirurgia(crgSeq);
		return this.formatarNomeComPonto(nomePaciente);
	}

	/**
	 * O último sobrenome do paciente deverá ser exibido com a primeira letra do mesmo precedido de ponto
	 * 
	 * @param nomePaciente
	 * @return
	 */
	private String formatarNomeComPonto(String nomePaciente) {
		nomePaciente = StringUtils.trim(nomePaciente);
		if (StringUtils.isEmpty(nomePaciente)) {
			//throw new IllegalArgumentException("O nome do paciente não informado!");
			return "ERRO";
		}
		return nomePaciente.substring(0, nomePaciente.lastIndexOf(' ') + 2) + ".";
	}

	/*
	 * Getters Facades, RNs e DAOs
	 */

	protected MbcCirurgiasDAO getMbcCirurgiasDAO() {
		return mbcCirurgiasDAO;
	}

}
