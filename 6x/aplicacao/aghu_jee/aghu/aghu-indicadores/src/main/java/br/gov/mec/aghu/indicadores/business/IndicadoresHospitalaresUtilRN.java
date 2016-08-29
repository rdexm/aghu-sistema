package br.gov.mec.aghu.indicadores.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.model.AinMovimentosInternacao;
import br.gov.mec.aghu.core.business.BaseBusiness;

/**
 * Classe para implementação da package AINK_IH_UTIL
 */
@Stateless
public class IndicadoresHospitalaresUtilRN extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(IndicadoresHospitalaresUtilRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IInternacaoFacade internacaoFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = -7000120663576243469L;

	protected IInternacaoFacade getInternacaoFacade() {
		return this.internacaoFacade;
	}

	// TODO Implementar functions não implementadas da package AINK_IH_UTIL

	/**
	 * Método para retornar a data final do movimento
	 * 
	 * ORADB Function AINK_IH_UTIL.DTHR_FINAL_M
	 * 
	 * @param seqInternacao
	 * @param dataLancamento
	 * @param dataAlta
	 * @return
	 */
	public Date obterDataFinalMovimento(Integer seqInternacao, Date dataLancamento, Date dataAlta) {
		List<AinMovimentosInternacao> movimentoInternacaoList = this.getInternacaoFacade()
				.pesquisarMovimentoInternacao(seqInternacao, dataLancamento, dataAlta);
		Date dataFinal = null;

		if (!movimentoInternacaoList.isEmpty()) {
			AinMovimentosInternacao movimentoInternacao = movimentoInternacaoList.get(0);
			dataFinal = movimentoInternacao.getDthrLancamento();
		}

		return dataFinal;
	}

	/**
	 * Método para retornar o código do tipo de movimento de internação do
	 * movimento de internação
	 * 
	 * ORADB Function AINK_IH_UTIL.TMI_SEQ_T
	 * 
	 * @param seqInternacao
	 * @param data
	 * @return
	 */
	public Integer obterTipoMovimentoId(Integer seqInternacao, Date data) {
		List<AinMovimentosInternacao> movimentoInternacaoList = this.getInternacaoFacade()
				.pesquisarMovimentoInternacao(seqInternacao, data, data);

		Integer codigoTipoMovimentoInternacao = null;

		if (!movimentoInternacaoList.isEmpty()) {
			codigoTipoMovimentoInternacao = movimentoInternacaoList.get(0)
					.getTipoMovimentoInternacao().getCodigo();
		}

		// Itera a lista e retorna o ultimo movimento de internação encontrado
		// para a unidade funcional com código 125
		for (AinMovimentosInternacao movimentoInternacao : movimentoInternacaoList) {
			if (movimentoInternacao.getUnidadeFuncional() != null
					&& movimentoInternacao.getUnidadeFuncional().getSeq()
							.equals(Short.valueOf("125"))) {
				codigoTipoMovimentoInternacao = movimentoInternacao.getTipoMovimentoInternacao()
						.getCodigo();
			} else {
				break;
			}
		}

		return codigoTipoMovimentoInternacao;
	}

	/**
	 * Método para retornar a especialidade do movimento
	 * 
	 * ORADB Function AINK_IH_UTIL.ESP_SEQ
	 * 
	 * @param seqInternacao
	 * @param dataLancamento
	 * @param dataAlta
	 * @return
	 */
	public Short obterEspecialidadeId(Integer seqInternacao, Date data) {
		List<AinMovimentosInternacao> movimentoInternacaoList = this.getInternacaoFacade()
				.pesquisarMovimentoInternacao(seqInternacao, data, data);

		Short seqEspecialidade = null;

		if (!movimentoInternacaoList.isEmpty()) {
			AinMovimentosInternacao movimentoInternacao = movimentoInternacaoList.get(0);
			if (movimentoInternacao.getEspecialidade() != null) {
				seqEspecialidade = movimentoInternacao.getEspecialidade().getSeq();
			}
		}

		return seqEspecialidade;
	}

	/**
	 * Método para retornar a unidade do movimento
	 * 
	 * ORADB Function AINK_IH_UTIL.UNIDADE
	 * 
	 * @param seqInternacao
	 * @param dataLancamento
	 * @param dataAlta
	 * @return
	 */
	public Short obterUnidadeId(Integer seqInternacao, Date data) {
		List<AinMovimentosInternacao> movimentoInternacaoList = this.getInternacaoFacade()
				.pesquisarMovimentoInternacao(seqInternacao, data, data);

		Short seqUnidade = null;

		if (!movimentoInternacaoList.isEmpty()) {
			AinMovimentosInternacao movimentoInternacao = movimentoInternacaoList.get(0);
			if (movimentoInternacao.getUnidadeFuncional() != null) {
				seqUnidade = movimentoInternacao.getUnidadeFuncional().getSeq();
			}
		}

		return seqUnidade;
	}

}
