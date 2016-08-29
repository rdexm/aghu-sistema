package br.gov.mec.aghu.internacao.business;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.internacao.dao.AinExtratoLeitosDAO;
import br.gov.mec.aghu.model.AinExtratoLeitos;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.core.business.BaseBusiness;

/**
 * Classe para implementação da package AINK_LEITOS_DESAT
 */
@Stateless
public class LeitoDesativadoRN extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(LeitoDesativadoRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AinExtratoLeitosDAO ainExtratoLeitosDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 7083519041644447558L;

	/**
	 * Método com query representada pelo cursor "data_b" da package
	 * AINK_LEITOS_DESAT
	 * 
	 * @param leitoId
	 * @param data
	 * @return
	 */
	private List<AinExtratoLeitos> pesquisarDataBloqueio(String leitoId,
			Date data) {
		return this.getAinExtratoLeitosDAO().pesquisarDataBloqueioGrupoD(leitoId, data);
	}

	/**
	 * Método com query representada pelo cursor "data_l" da package
	 * AINK_LEITOS_DESAT
	 * 
	 * @param leitoId
	 * @param data
	 * @return
	 */
	private List<AinExtratoLeitos> pesquisarDataLiberacao(String leitoId,
			Date data) {
		return this.getAinExtratoLeitosDAO().pesquisarDataLiberacao(leitoId, data);
	}

	/**
	 * Método com query representada pelo cursor "data_i" da package
	 * AINK_LEITOS_DESAT
	 * 
	 * @param leitoId
	 * @param data
	 * @return
	 */
	private List<AinExtratoLeitos> pesquisarMovimentacaoLeito(String leitoId,
			Date data) {

		return this.getAinExtratoLeitosDAO().pesquisarMovimentacaoLeito(leitoId, data);
	}

	/**
	 * Método com query representada pelo cursor "leito" da package
	 * AINK_LEITOS_DESAT
	 * 
	 * @param leitoId
	 * @return
	 */
	private AinLeitos obterLeitoBloqueado(String leitoId) {
		AinLeitos retorno = null;
		if (leitoId != null) {
			retorno = this.getAinExtratoLeitosDAO().obterLeitoDesativado(leitoId);
		}

		return retorno;
	}

	/**
	 * Método para retornar a data final do lançamento
	 * 
	 * ORADB Function AINK_LEITOS_DESAT.DTHR_FINAL_L
	 * 
	 * @param leitoId
	 * @param data
	 * @return
	 */
	public Date obterDataFinalLancamento(String leitoId, Date data) {
		List<AinExtratoLeitos> extratoLeitoList = this.pesquisarDataLiberacao(
				leitoId, data);
		Date dataLancamento = null;

		if (extratoLeitoList != null && extratoLeitoList.size() > 0) {
			dataLancamento = extratoLeitoList.get(0).getDthrLancamento();
		}

		Calendar cal = Calendar.getInstance();
		cal.setTime(data);
		cal.set(Calendar.DAY_OF_MONTH,
				cal.getActualMaximum(Calendar.DAY_OF_MONTH));
		
		if (dataLancamento != null && dataLancamento.compareTo(cal.getTime()) > 0) {
			// Seta o dia para o último do mês (28/29 em fevereiro, 30/31 nos
			// demais meses)
			cal.add(Calendar.DAY_OF_MONTH, 1);

			return cal.getTime();
		} else {
			return dataLancamento;
		}
	}

	/**
	 * Método para retornar a data de início
	 * 
	 * ORADB Function AINK_LEITOS_DESAT.DTHR_INICIO
	 * 
	 * @param leitoId
	 * @param data
	 * @return
	 */
	public Date obterDataInicial(String leitoId, Date data) {
		Date retorno = null;
		Date dataLancamento = null;

		// Abre cursor "data_i"
		List<AinExtratoLeitos> extratoLeitoList = this
				.pesquisarMovimentacaoLeito(leitoId, data);
		
		if (extratoLeitoList != null && !extratoLeitoList.isEmpty()) {
			dataLancamento = extratoLeitoList.get(0).getDthrLancamento();
		}

		if (extratoLeitoList == null || extratoLeitoList.isEmpty()) {
			// Abre cursor "data_b"
			extratoLeitoList = this.pesquisarDataBloqueio(leitoId, data);

			if (extratoLeitoList == null || extratoLeitoList.isEmpty()) {
				// Abre cursor "leito"
				AinLeitos leito = this.obterLeitoBloqueado(leitoId);
				if (leito != null) {
					retorno = data;
				}
			} else {
				retorno = null;
			}
		} else {
			// Abre cursor "data_l"
			extratoLeitoList = this.pesquisarDataLiberacao(leitoId, DateUtils.truncate(dataLancamento, Calendar.DAY_OF_MONTH));

			Calendar cal = Calendar.getInstance();
			cal.setTime(data);
			cal.set(Calendar.DAY_OF_MONTH,
					cal.getActualMaximum(Calendar.DAY_OF_MONTH));
			
			if (extratoLeitoList != null && !extratoLeitoList.isEmpty()
					&& extratoLeitoList.get(0).getDthrLancamento() != null
					&& extratoLeitoList.get(0).getDthrLancamento()
							.compareTo(cal.getTime()) < 0) {
				retorno = null;
			} else {
				retorno = data;
			}
		}

		return retorno;
	}

	/**
	 * Método para retornar a situação do leito do paciente
	 * 
	 * ORADB Function AINK_LEITOS_DESAT.IND_BLOQUEIO_PACIENTE
	 * 
	 * @param leitoId
	 * @param data
	 * @return
	 */
	public DominioSimNao obterSituacaoPaciente(String leitoId, Date data) {
		DominioSimNao retorno = null;

		// Abre cursor "data_i"
		List<AinExtratoLeitos> extratoLeitoList = this
				.pesquisarMovimentacaoLeito(leitoId, data);
		
		if ((extratoLeitoList != null && !extratoLeitoList.isEmpty()) && extratoLeitoList.get(0) != null
				&& extratoLeitoList.get(0).getTipoMovimentoLeito() != null) {
			retorno = extratoLeitoList.get(0).getTipoMovimentoLeito()
					.getIndBloqueioPaciente();
		}

		if (extratoLeitoList == null || extratoLeitoList.isEmpty()) {

			// Abre cursor "data_b"
			extratoLeitoList = this.pesquisarDataBloqueio(leitoId, data);
			if (extratoLeitoList == null || extratoLeitoList.isEmpty()) {

				// Abre cursor "leito"
				AinLeitos leito = this.obterLeitoBloqueado(leitoId);
				if (leito != null) {
					retorno = DominioSimNao.getInstance(leito.getIndBloqLeitoLimpeza());
				}
			}
			// Não precisa de else para retornar data nula, pois a mesma é
			// iniciada com null
		}
		return retorno;
	}

	protected AinExtratoLeitosDAO getAinExtratoLeitosDAO(){
		return ainExtratoLeitosDAO;
	}
}
