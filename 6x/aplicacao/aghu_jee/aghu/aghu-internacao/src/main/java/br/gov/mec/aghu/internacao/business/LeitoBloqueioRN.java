package br.gov.mec.aghu.internacao.business;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioMovimentoLeito;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.internacao.dao.AinExtratoLeitosDAO;
import br.gov.mec.aghu.model.AinExtratoLeitos;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.core.business.BaseBusiness;

/**
 * Classe para implementação da package AINK_LEITOS_BLQ
 */
@Stateless
public class LeitoBloqueioRN extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(LeitoBloqueioRN.class);

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
	private static final long serialVersionUID = -7920843217841837195L;

	/**
	 * Método com query representada pelo cursor "data_b" da package
	 * AINK_LEITOS_BLQ
	 * 
	 * @param leitoId
	 * @param data
	 * @return
	 */
	private List<AinExtratoLeitos> pesquisarDataBloqueio(String leitoId,
			Date data) {
		List<AinExtratoLeitos> retorno = null;
		Date dataLancamento = this.obterDataMinimaMovimentoLeito(leitoId, data,
				DominioMovimentoLeito.B, DominioMovimentoLeito.BI);

		if (dataLancamento != null) {
			retorno = this.getAinExtratoLeitosDAO().pesquisarDataBloqueio(leitoId, dataLancamento);
		}

		return retorno;
	}

	/**
	 * Método com query representada pelo cursor "data_l" da package
	 * AINK_LEITOS_BLQ
	 * 
	 * @param leitoId
	 * @param data
	 * @return
	 */
	private List<AinExtratoLeitos> pesquisarDataLiberacao(String leitoId,
			Date data) {
		List<AinExtratoLeitos> retorno = null;
		Date dataLancamento = this.obterDataMinimaMovimentoLeito(leitoId, data,
				DominioMovimentoLeito.L, DominioMovimentoLeito.BL);

		if (dataLancamento != null) {
			retorno = this.getAinExtratoLeitosDAO().pesquisarDataBloqueio(leitoId, dataLancamento);
		}

		return retorno;
	}

	/**
	 * Método com implementação de query genérica usada nos cursores data_b e
	 * data_l da package AINK_LEITOS_BLQ
	 * 
	 * @param leitoId
	 * @param data
	 * @param restricao1
	 * @param restricao2
	 * @return data mínima de movimentação do leito
	 */
	private Date obterDataMinimaMovimentoLeito(String leitoId, Date data,
			DominioMovimentoLeito restricao1, DominioMovimentoLeito restricao2) {
		Date retorno = null;

		if (data != null) {
			retorno = this.getAinExtratoLeitosDAO().obterDataMinimaMovimentoLeito(leitoId, data, restricao1, restricao2);
		}

		return retorno;
	}
	
	/**
	 * Método para buscar a data máxima da movimentação de leito de
	 * AinExtratoLeitos
	 * 
	 * @param leitoId
	 * @param data
	 * @param restricao1
	 * @param restricao2
	 * @return
	 */
	private Date obterDataMaximaMovimentoLeito(String leitoId, Date data) {
		Date retorno = null;

		if (data != null) {
			retorno = this.getAinExtratoLeitosDAO().obterDataMaximaMovimentoLeito(leitoId, data);
		}

		return retorno;
	}

	/**
	 * Método com query representada pelo cursor "data_i" da package
	 * AINK_LEITOS_BLQ
	 * 
	 * @param leitoId
	 * @param data
	 * @return
	 */
	private List<AinExtratoLeitos> pesquisarMovimentacaoLeito(String leitoId,
			Date data) {
		List<AinExtratoLeitos> retorno = null;
		Date dataLancamento = this.obterDataMaximaMovimentoLeito(leitoId, data);

		if (dataLancamento != null) {
			retorno = this.getAinExtratoLeitosDAO().pesquisarDataBloqueio(leitoId, dataLancamento);
		}

		return retorno;
	}

	/**
	 * Método com query representada pelo cursor "leito" da package
	 * AINK_LEITOS_BLQ
	 * 
	 * @param leitoId
	 * @return
	 */
	private AinLeitos obterLeitoBloqueado(String leitoId) {
		AinLeitos retorno = null;
		if (leitoId != null) {
			retorno = this.getAinExtratoLeitosDAO().obterLeitoBloqueado(leitoId);
		}

		return retorno;
	}

	/**
	 * Método para retornar a data final do lançamento
	 * 
	 * ORADB Function AINK_LEITOS_BLQ.DTHR_FINAL_L
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
			dataLancamento = DateUtils.truncate(extratoLeitoList.get(0).getDthrLancamento(), Calendar.SECOND);
		}

		Date primeiroDiaMesSeguinte = null; 
		Calendar cal = Calendar.getInstance();
		cal.setTime(data);
		// Seta o dia para o último do mês (28/29 em fevereiro, 30/31 nos
		// demais meses)
		cal.set(Calendar.DAY_OF_MONTH,
				cal.getActualMaximum(Calendar.DAY_OF_MONTH));
		// Adiciona um dia a data
		cal.add(Calendar.DAY_OF_MONTH, 1);
		cal.setTime(cal.getTime());
		primeiroDiaMesSeguinte = cal.getTime();
		
		
		if (dataLancamento != null && dataLancamento.compareTo(DateUtils.truncate(primeiroDiaMesSeguinte, Calendar.SECOND)) > 0) {
			return primeiroDiaMesSeguinte;
		} else {
			return dataLancamento;
		}
	}

	/**
	 * Método para retornar a data de início
	 * 
	 * ORADB Function AINK_LEITOS_BLQ.DTHR_INICIO
	 * 
	 * @param leitoId
	 * @param data
	 * @return
	 */
	public Date obterDataInicial(String leitoId, Date data) {
		Date retorno = null;
		DominioMovimentoLeito grupoMovimentoLeito = null;

		// Abre cursor "data_i"
		List<AinExtratoLeitos> extratoLeitoList = this
				.pesquisarMovimentacaoLeito(leitoId, data);

		if (extratoLeitoList != null && extratoLeitoList.size() > 0) {
			grupoMovimentoLeito = extratoLeitoList.get(0)
					.getTipoMovimentoLeito().getGrupoMvtoLeito();

			retorno = data;

			if (!DominioMovimentoLeito.B.equals(grupoMovimentoLeito)
					&& !(DominioMovimentoLeito.BI.equals(grupoMovimentoLeito))) {

				// Abre cursor "data_b"
				extratoLeitoList = this.pesquisarDataBloqueio(leitoId, data);
				if (extratoLeitoList != null && !extratoLeitoList.isEmpty()) {
					retorno = extratoLeitoList.get(0).getDthrLancamento();
				}

				if (retorno == null) {

					// Abre cursor "leito"
					AinLeitos leito = this.obterLeitoBloqueado(leitoId);
					if (leito != null) {
						retorno = data;
					}
				} else {
					retorno = null;
				}
			}
		}
		return retorno;
	}

	/**
	 * Método para retornar a situação do leito do paciente
	 * 
	 * ORADB Function AINK_LEITOS_BLQ.IND_BLOQUEIO_PACIENTE
	 * 
	 * @param leitoId
	 * @param data
	 * @return
	 */
	public DominioSimNao obterSituacaoPaciente(String leitoId, Date data) {
		DominioSimNao retorno = null;
		DominioMovimentoLeito grupoMovimentoLeito = null;

		// Abre cursor "data_i"
		List<AinExtratoLeitos> extratoLeitoList = this
				.pesquisarMovimentacaoLeito(leitoId, data);

		if (extratoLeitoList != null && extratoLeitoList.size() > 0) {
			grupoMovimentoLeito = extratoLeitoList.get(0)
					.getTipoMovimentoLeito().getGrupoMvtoLeito();
			if (extratoLeitoList.get(0).getTipoMovimentoLeito() != null) {
				retorno = extratoLeitoList.get(0).getTipoMovimentoLeito()
						.getIndBloqueioPaciente();
			}

			if (!DominioMovimentoLeito.B.equals(grupoMovimentoLeito)
					&& !(DominioMovimentoLeito.BI.equals(grupoMovimentoLeito))) {

				// Abre cursor "data_b"
				extratoLeitoList = this.pesquisarDataBloqueio(leitoId, data);
				
				if (extratoLeitoList == null || extratoLeitoList.isEmpty()) {
					
					// Abre cursor "leito"
					AinLeitos leito = this.obterLeitoBloqueado(leitoId);
					if (leito != null) {
						retorno = DominioSimNao.getInstance(leito.getIndBloqLeitoLimpeza());
					}
				} else {
					if (extratoLeitoList.get(0).getTipoMovimentoLeito() != null) {
						retorno = extratoLeitoList.get(0)
								.getTipoMovimentoLeito()
								.getIndBloqueioPaciente();
					}
				}
				// Não precisa de else para retornar data nula, pois a mesma é
				// iniciada com null
			}
		}
		return retorno;
	}

	/**
	 * Método para retornar o código do tipo de movimento do leito
	 * 
	 * ORADB Function AINK_LEITOS_BLQ.COD_BLOQUEIO
	 * 
	 * @param leitoId
	 * @param data
	 * @return
	 */
	public Short obterCodigoBloqueio(String leitoId, Date data) {
		Short retorno = null;
		Date dataLancamento = null;
		DominioMovimentoLeito grupoMovimentoLeito = null;

		// Abre cursor "data_i"
		List<AinExtratoLeitos> extratoLeitoList = this
				.pesquisarMovimentacaoLeito(leitoId, data);

		if (extratoLeitoList != null && extratoLeitoList.size() > 0) {
			
			dataLancamento = extratoLeitoList.get(0).getDthrLancamento(); 
			if (extratoLeitoList.get(0).getTipoMovimentoLeito() != null) {
				retorno = extratoLeitoList.get(0).getTipoMovimentoLeito()
						.getCodigo();
			}
			
			grupoMovimentoLeito = extratoLeitoList.get(0)
					.getTipoMovimentoLeito().getGrupoMvtoLeito();

			if (!DominioMovimentoLeito.B.equals(grupoMovimentoLeito)
					&& !(DominioMovimentoLeito.BI.equals(grupoMovimentoLeito))) {

				// Abre cursor "data_b"
				extratoLeitoList = this.pesquisarDataBloqueio(leitoId, data);
				if (extratoLeitoList != null && !extratoLeitoList.isEmpty()) {
					dataLancamento = extratoLeitoList.get(0).getDthrLancamento();
				}
				
				
				if (dataLancamento == null) {

					// Abre cursor "leito"
					AinLeitos leito = this.obterLeitoBloqueado(leitoId);
					if (leito != null) {
						retorno = leito.getTipoMovimentoLeito().getCodigo();
					}
				} else {
					retorno = null;
				}
			}
		}
		return retorno;
	}

	protected AinExtratoLeitosDAO getAinExtratoLeitosDAO(){
		return ainExtratoLeitosDAO;
	}
}
