package br.gov.mec.aghu.blococirurgico.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.joda.time.Duration;

import br.gov.mec.aghu.blococirurgico.dao.MbcCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.vo.CirurgiaTelaVO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateConstants;
import br.gov.mec.aghu.core.utils.DateUtil;

/**
 * ORADB MBCF_ATU_CIRURGIA_NS.EVT_WHEN_VALIDATE_ITEM Executada ao alterar o valor de “Confirma Digitação NS” em de #24941: Registro de cirurgia realizada e nota de consumo
 * 
 * @author aghu
 * 
 */
@Stateless
public class VerificaColisaoDigitacaoNSON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(VerificaColisaoDigitacaoNSON.class);

	@Override
	@Deprecated
	public Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcCirurgiasDAO mbcCirurgiasDAO;


	@EJB
	private IAghuFacade iAghuFacade;

	private static final long serialVersionUID = 3672734082737613246L;

	public enum VerificaColisaoDigitacaoNSRNExceptionCode implements BusinessExceptionCode {
		MBC_01097, MBC_01335, MENSAGEM_COLISAO_HORARIOS_NS_JA_DIGITADA_PACIENTE;
	}

	/**
	 * ORADB PROCEDURE MBCF_ATU_CIRURGIA_NS.EVT_WHEN_VALIDATE_ITEM
	 * 
	 * <p>
	 * Executada ao alterar o valor de “Confirma Digitação NS” em de #24941: Registro de cirurgia realizada e nota de consumo
	 * <p>
	 * 
	 * @param vo
	 * @throws ApplicationBusinessException 
	 */
	public String verificarColisaoDigitacaoNS(final CirurgiaTelaVO vo) throws ApplicationBusinessException {
		this.validarColisaoHorariosOutraNSDigitadaEntrada(vo.getCirurgia()); // RN1
		this.validarColisaoHorariosOutraNSDigitadaSaida(vo.getCirurgia()); // RN2
		return this.validarOcupacaoSalaTempoMinimo(vo); // RN3 - pode retornar warnings
	}

	/**
	 * Verifica colisão de horários com outra nota de sala já digitada - ENTRADA NA SALA
	 * 
	 * <p>
	 * RN1
	 * <p>
	 * 
	 * @param crg
	 */
	public void validarColisaoHorariosOutraNSDigitadaEntrada(final MbcCirurgias crg) throws ApplicationBusinessException {
		if (crg.getDataEntradaSala() != null) {

			// CURSOR C_COLISAO: Verifica colisão de horários outra nota já digitada na mesma UNF/DATA/SALA
			List<MbcCirurgias> listaColisao = getMbcCirurgiasDAO().pesquisarColisaoHorariosOutraNSDigitada(crg.getUnidadeFuncional().getSeq(), crg.getData(), crg.getSeq(),
					crg.getSalaCirurgica().getId().getUnfSeq(), crg.getSalaCirurgica().getId().getSeqp());

			for (MbcCirurgias colisao : listaColisao) {

				final boolean validacaoDataColisao1 = DateUtil.validaDataMaiorIgual(crg.getDataEntradaSala(), colisao.getDataEntradaSala())
				&& DateUtil.validaDataMenor(crg.getDataEntradaSala(), colisao.getDataSaidaSala());
				final boolean validacaoDataColisao2 = DateUtil.validaDataMenor(crg.getDataEntradaSala(), colisao.getDataEntradaSala())
				&& DateUtil.validaDataMaior(crg.getDataSaidaSala(), colisao.getDataSaidaSala());

				if (validacaoDataColisao1 || validacaoDataColisao2) {
					throw obterExcessaoColisaoHorarios(colisao);
				}

			}

		}
	}

	/**
	 * Verifica colisão de horários com outra nota de sala já digitada - SAÍDA DA SALA
	 * 
	 * <p>
	 * RN2
	 * <p>
	 * 
	 * @param crg
	 */
	public void validarColisaoHorariosOutraNSDigitadaSaida(final MbcCirurgias crg) throws ApplicationBusinessException {
		if (crg.getDataSaidaSala() != null) {
			// CURSOR C_COLISAO: Verifica colisão de horários outra nota já digitada na mesma UNF/DATA/SALA
			List<MbcCirurgias> listaColisao = getMbcCirurgiasDAO().pesquisarColisaoHorariosOutraNSDigitada(crg.getUnidadeFuncional().getSeq(), crg.getData(), crg.getSeq(),
					crg.getSalaCirurgica().getId().getUnfSeq(), crg.getSalaCirurgica().getId().getSeqp());

			for (MbcCirurgias colisao : listaColisao) {

				final boolean validacaoDataColisao1 = DateUtil.validaDataMaior(crg.getDataSaidaSala(), colisao.getDataEntradaSala())
				&& DateUtil.validaDataMenorIgual(crg.getDataSaidaSala(), colisao.getDataSaidaSala());
				if (validacaoDataColisao1) {
					throw obterExcessaoColisaoHorarios(colisao);
				}

				final boolean validacaoDataColisao2 = DateUtil.validaDataMenor(crg.getDataEntradaSala(), colisao.getDataEntradaSala())
				&& DateUtil.validaDataMaior(crg.getDataSaidaSala(), colisao.getDataSaidaSala());
				if (validacaoDataColisao2) {
					throw obterExcessaoColisaoHorarios(colisao);
				}
			}
		}
	}

	/**
	 * Verifica a ocupação e tempo mínimo da sala
	 * 
	 * <p>
	 * RN3
	 * <p>
	 * 
	 * @param vo
	 * @throws ApplicationBusinessException 
	 */
	public String validarOcupacaoSalaTempoMinimo(final CirurgiaTelaVO vo) throws ApplicationBusinessException {
		final MbcCirurgias crg = vo.getCirurgia();
		if (crg.getDataEntradaSala() != null && crg.getDataSaidaSala() != null) {
			MbcProcedimentoCirurgicos procedimentoPrincipal = vo.obterProcedimentoPrincipal().getProcedimentoCirurgico();

			if (procedimentoPrincipal.getTempoMinimo() != null && !CoreUtil.igual(procedimentoPrincipal.getTempoMinimo(), Short.valueOf("0"))) {
				Duration duracao = new Duration(new DateTime(crg.getDataEntradaSala()), new DateTime(crg.getDataSaidaSala()));
				long v_duracao = duracao.getStandardMinutes(); // Em minutos
				
				int tempoMinimoHoras = ( (int) procedimentoPrincipal.getTempoMinimo() / 100);
				int tempoMinimoMinutos = procedimentoPrincipal.getTempoMinimo() % 100;
				long v_tempo_minimo = (tempoMinimoHoras * 60) + tempoMinimoMinutos; //Em minutos
				
				if (CoreUtil.maior(v_duracao, (2 * v_tempo_minimo))) {
					// O tempo de ocupação da sala ultrapassa o dobro de tempo considerado mínimo.
					return new ApplicationBusinessException(VerificaColisaoDigitacaoNSRNExceptionCode.MBC_01097).getMessage();
				}

				if (CoreUtil.menor(v_duracao, v_tempo_minimo)) {
					// O tempo de ocupação da sala é menor que o tempo considerado mínimo.
					return new ApplicationBusinessException(VerificaColisaoDigitacaoNSRNExceptionCode.MBC_01335).getMessage();
				}
			}
		}
		
		return null;
	}

	/**
	 * Obtem a exceção de negócio para colisão de cirurgias
	 * 
	 * @param crgTela
	 * @param colisao
	 * @return
	 */
	private ApplicationBusinessException obterExcessaoColisaoHorarios(final MbcCirurgias colisao) {
		String nomePaciente = colisao.getPaciente().getNome();
		String sala = colisao.getSalaCirurgica().getNome();
		Short numeroAgenda = colisao.getNumeroAgenda();
		String entrada = DateUtil.obterDataFormatada(colisao.getDataEntradaSala(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO);
		String saida = DateUtil.obterDataFormatada(colisao.getDataSaidaSala(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO);
		return new ApplicationBusinessException(VerificaColisaoDigitacaoNSRNExceptionCode.MENSAGEM_COLISAO_HORARIOS_NS_JA_DIGITADA_PACIENTE, nomePaciente, sala, numeroAgenda, entrada,
				saida);
	}

	/*
	 * Getters Facades, RNs e DAOs
	 */

	public IAghuFacade getAghuFacade() {
		return this.iAghuFacade;
	}

	public MbcCirurgiasDAO getMbcCirurgiasDAO() {
		return mbcCirurgiasDAO;
	}

}
