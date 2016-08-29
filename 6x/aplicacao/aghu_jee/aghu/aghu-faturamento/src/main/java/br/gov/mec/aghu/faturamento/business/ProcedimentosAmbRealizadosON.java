package br.gov.mec.aghu.faturamento.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioOrigemProcedimentoAmbulatorialRealizado;
import br.gov.mec.aghu.dominio.DominioSituacaoProcedimentoAmbulatorio;
import br.gov.mec.aghu.faturamento.dao.FatMetaDAO;
import br.gov.mec.aghu.faturamento.dao.FatProcedAmbRealizadoDAO;
import br.gov.mec.aghu.faturamento.vo.FatVariaveisVO;
import br.gov.mec.aghu.faturamento.vo.PreviaDiariaFaturamentoVO;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.FatCompetencia;
import br.gov.mec.aghu.model.FatMeta;
import br.gov.mec.aghu.model.FatProcedAmbRealizado;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ProcedimentosAmbRealizadosON extends BaseBusiness {


@EJB
private FaturamentoAmbulatorioON faturamentoAmbulatorioON;

@EJB
private ProcedimentosAmbRealizadoRN procedimentosAmbRealizadoRN;

private static final Log LOG = LogFactory.getLog(ProcedimentosAmbRealizadosON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private FatProcedAmbRealizadoDAO fatProcedAmbRealizadoDAO;

@EJB
private IParametroFacade parametroFacade;

@EJB
private IInternacaoFacade internacaoFacade;

@Inject
private FatMetaDAO fatMetaDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 2138474955201428248L;

	public enum ProcedimentosAmbRealizadoONExceptionCode implements BusinessExceptionCode {
		MSG_SITUACAO_SOMENTE_ABERTO_CANCELADO ; //Somente é possível alterar a situação para Aberto ou Cancelado.
	}

	protected ProcedimentosAmbRealizadoRN getProcedimentosAmbRealizadoRN() {
		return procedimentosAmbRealizadoRN;
	}

	public void excluirProcedimentosAmbulatoriaisRealizadosPorAtendimento(AghAtendimentos atendimento) {

		FatProcedAmbRealizadoDAO dao = this.getFatProcedAmbRealizadoDAO();

		List<FatProcedAmbRealizado> procedimentosRealizados = dao.buscarProcedimentosRealizadosPorAtendimento(atendimento);

		for (FatProcedAmbRealizado procedimento : procedimentosRealizados) {
			dao.remover(procedimento);
		}
	}

	public void excluirProcedimentoAmbulatorialRealizado(final FatProcedAmbRealizado procedAmbRealizado, String nomeMicrocomputador, final Date dataFimVinculoServidor, FatVariaveisVO fatVariaveisVO) throws BaseException {
		// chama triggers FATT_PMR_BRD e FATT_PMR_ARD
		ProcedimentosAmbRealizadoRN ambRealizadoRN = getProcedimentosAmbRealizadoRN();
		ambRealizadoRN.brdPreRemocaoRow(procedAmbRealizado, nomeMicrocomputador, dataFimVinculoServidor);
		getFatProcedAmbRealizadoDAO().remover(procedAmbRealizado);
		ambRealizadoRN.ardPosRemocaoRow(procedAmbRealizado, nomeMicrocomputador, dataFimVinculoServidor, fatVariaveisVO);
	}

	public void atualizarProcedimentoAmbulatorialRealizado(final FatProcedAmbRealizado procedAmbRealizado,
			final FatProcedAmbRealizado oldProcedAmbRealizado, String nomeMicrocomputador, final Date dataFimVinculoServidor,
			FatVariaveisVO fatVariaveisVO) throws BaseException {
		// chama triggers FATT_PMR_BRU, FATT_PMR_BASE_BRU, FATT_PMR_BSU e
		// FATT_PMR_ARU
		ProcedimentosAmbRealizadoRN ambRealizadoRN = getProcedimentosAmbRealizadoRN();
		ambRealizadoRN.bsuPreAtualizacaoStatement(oldProcedAmbRealizado, procedAmbRealizado);
		ambRealizadoRN.bruPreAtualizacaoRow(oldProcedAmbRealizado, procedAmbRealizado, nomeMicrocomputador, dataFimVinculoServidor);
		getFatProcedAmbRealizadoDAO().atualizar(procedAmbRealizado);
		ambRealizadoRN.asuPosAtualizacaoStatement(oldProcedAmbRealizado, procedAmbRealizado, nomeMicrocomputador, dataFimVinculoServidor);
		ambRealizadoRN.aruPosAtualizacaoRow(oldProcedAmbRealizado, procedAmbRealizado, nomeMicrocomputador, dataFimVinculoServidor, fatVariaveisVO);
	}


	/**
	 * #45289: if para impedir que procedimentos sejam inseridos para consultas da UBS
	 * @param fatProcedAmbRealizado
	 * @param nomeMicrocomputador
	 * @param servidorLogado
	 * @param dataFimVinculoServidor
	 * @throws MECBaseException
	 */
	public void inserirFatProcedAmbRealizado( FatProcedAmbRealizado fatProcedAmbRealizado, String nomeMicrocomputador,  Date dataFimVinculoServidor) throws BaseException {
		if (!verificaPlanoUBS(fatProcedAmbRealizado)){
			ProcedimentosAmbRealizadoRN ambRealizadoRN = getProcedimentosAmbRealizadoRN();
			fatProcedAmbRealizado.setCriadoPor("AGHU");
			ambRealizadoRN.briPreInsercaoRow(fatProcedAmbRealizado, nomeMicrocomputador, dataFimVinculoServidor);
			getFatProcedAmbRealizadoDAO().persistir(fatProcedAmbRealizado);
			ambRealizadoRN.posInsercaoRow(fatProcedAmbRealizado, nomeMicrocomputador, dataFimVinculoServidor);
			getFatProcedAmbRealizadoDAO().flush();
		}
	}
	
	public void inserirFatProcedAmbRealizadoAposVerificarEvolucaoEAnamnese( FatProcedAmbRealizado fatProcedAmbRealizado, String nomeMicrocomputador,  Date dataFimVinculoServidor) throws BaseException {
		
			ProcedimentosAmbRealizadoRN ambRealizadoRN = getProcedimentosAmbRealizadoRN();
			ambRealizadoRN.briPreInsercaoRow(fatProcedAmbRealizado, nomeMicrocomputador, dataFimVinculoServidor);
			getFatProcedAmbRealizadoDAO().persistir(fatProcedAmbRealizado);
			ambRealizadoRN.posInsercaoRow(fatProcedAmbRealizado, nomeMicrocomputador, dataFimVinculoServidor);
			getFatProcedAmbRealizadoDAO().flush();
		
	}
	
	/**
	 * #45289
	 * @param fatProcedAmbRealizado
	 * @return
	 */
	private Boolean verificaPlanoUBS(final FatProcedAmbRealizado fatProcedAmbRealizado) {
		Boolean retorno = false;
		if (DominioOrigemProcedimentoAmbulatorialRealizado.CON.equals(fatProcedAmbRealizado.getIndOrigem())){
			if (getInternacaoFacade().verificarCaracteristicaUnidadeFuncional(
					fatProcedAmbRealizado.getUnidadeFuncional().getSeq(), ConstanteAghCaractUnidFuncionais.UBS)){
				retorno = true;
			}
		}
		return retorno;
	}

	/**
	 * ATENCAO: Somente chamar esse metodo para persistir se for por tela, pois tem regra de validacao de tela
	 * 
	 * Sem a validacao da tela chamar inserir, atualizar, excluir dessa classe que estao acima
	 * 
	 * @param procedAmbRealizado
	 * @param oldProcedAmbRealizado
	 * @param dataFimVinculoServidor 
	 * @throws BaseException
	 */
	public void persistirProcedimentoAmbulatorialRealizado(final FatProcedAmbRealizado procedAmbRealizado,
			final FatProcedAmbRealizado oldProcedAmbRealizado, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {
		if (procedAmbRealizado != null) {
			
			if (!procedAmbRealizado.getSituacao().equals(DominioSituacaoProcedimentoAmbulatorio.ABERTO) && 
				!procedAmbRealizado.getSituacao().equals(DominioSituacaoProcedimentoAmbulatorio.CANCELADO)) {
				throw new ApplicationBusinessException(ProcedimentosAmbRealizadoONExceptionCode.MSG_SITUACAO_SOMENTE_ABERTO_CANCELADO);
			}
			
			if (procedAmbRealizado.getSeq() == null) {
				this.inserirFatProcedAmbRealizado(procedAmbRealizado, nomeMicrocomputador, dataFimVinculoServidor);
			} else {
				this.atualizarProcedimentoAmbulatorialRealizado(procedAmbRealizado, oldProcedAmbRealizado, nomeMicrocomputador, dataFimVinculoServidor, null);
			}
		}
	}

	public FatProcedAmbRealizado clonarFatProcedAmbRealizado(final FatProcedAmbRealizado procedAmbRealizado) throws BaseException {
		try {
			return (FatProcedAmbRealizado) BeanUtils.cloneBean(procedAmbRealizado);
		} catch (Exception e) {
			throw new ApplicationBusinessException(null);
		}
	}
	
	public List<PreviaDiariaFaturamentoVO> obterPreviaDiariaFaturamento(final FatCompetencia competencia, final boolean isPDF) throws ApplicationBusinessException{
		final Short vUnf101 = (Short) getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_UNID_AMBULATORIO).getVlrNumerico().shortValue();
		final Short vUnf33 = (Short) getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_UNIDADE_ZONA14).getVlrNumerico().shortValue();
		
		if(isPDF){
			final List<PreviaDiariaFaturamentoVO> lista = new ArrayList<PreviaDiariaFaturamentoVO>();

			lista.addAll(obterPreviaDiariaFaturamentoVOFormatado(competencia, isPDF, vUnf101, vUnf33, "ALT", "ALTA")); 			// Q1
			lista.addAll(obterPreviaDiariaFaturamentoVOFormatado(competencia, isPDF, vUnf101, vUnf33, "MDO", "MÉDIO")); 		// Q3
			lista.addAll(obterPreviaDiariaFaturamentoVOFormatado(competencia, isPDF, vUnf101, vUnf33, "PAB", "PAB")); 			// Q2
			lista.addAll(obterPreviaDiariaFaturamentoVOFormatado(competencia, isPDF, vUnf101, vUnf33, "EST", "ESTRATÉGICO")); 	// Q_EST
			
			atualizarInformacoesMeta(lista, competencia.getId().getMes(), competencia.getId().getAno());
			
			return lista;
			
		} else {
			final Short vCodCC = (Short) getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_COD_CC_PERDAS_INUTILIZACOES).getVlrNumerico().shortValue();
			final List<PreviaDiariaFaturamentoVO> lista = getFatProcedAmbRealizadoDAO()
					.obterPreviaDiariaFaturamento(competencia, isPDF, vUnf101,
							vUnf33, vCodCC, null);
			
			atualizarInformacoesMeta(lista, competencia.getId().getMes(), competencia.getId().getAno());
			
			return lista;
		}
	}

	private List<PreviaDiariaFaturamentoVO> obterPreviaDiariaFaturamentoVOFormatado(final FatCompetencia competencia, final boolean isPDF, final Short vUnf101, final Short vUnf33, String cctCodigo, String cctCodigoDesc) {
		List<PreviaDiariaFaturamentoVO> result = getFatProcedAmbRealizadoDAO().obterPreviaDiariaFaturamento(competencia, isPDF, vUnf101, vUnf33, null, cctCodigo);		
		for(PreviaDiariaFaturamentoVO vo : result){
			vo.setCctCodigoDesc(cctCodigoDesc);
			vo.setCctCodigo(cctCodigo);
		}
		
		return result;
	}
	
	
	/**
	 * Obtém as informações de meta (teto) para quantidade e valor de cada procedimento. 
	 * Atualiza a lista de VO passada como argumento.
	 */
	private void atualizarInformacoesMeta(List<PreviaDiariaFaturamentoVO> listaPreviaDiariaFaturamentoVO, Integer cpeMes, Integer cpeAno) {
		if (listaPreviaDiariaFaturamentoVO == null) {
			throw new IllegalArgumentException("O parametro listaPreviaDiariaFaturamentoVO não deve ser nulo.");
		}
		
		FatMetaDAO fatMetaDAO = getFatMetaDAO();
		FaturamentoAmbulatorioON faturamentoAmbulatorioON = getFaturamentoAmbulatorioON();
		
		for (PreviaDiariaFaturamentoVO vo : listaPreviaDiariaFaturamentoVO) {
			if (vo.getFcfSeq() != null) {
				//FatFormaOrganizacaoFinanceira formaOrganizacaoFinanceira  = getFatMetaDAO().obterPorChavePrimaria(new FatFormaOrganizacaoFinanceiraId(vo.getGrupoSeq(), vo.getSubGrupoSeq(), vo.getFormaOrganizacaoCodigo(), vo.getCaracteristicaFinanciamentoSeq()));
				List<FatMeta> listaMeta = fatMetaDAO.listarMetaPeloGrupoPeloSubGrupoPelaFormOrgPeloFinancPeloProced(vo.getGrupo(), vo.getSubGrupo(), vo.getFogCod(), vo.getFcfSeq(), vo.getIphPhoSeq(), vo.getIphSeq());
				if (!listaMeta.isEmpty()) {
					FatMeta meta = faturamentoAmbulatorioON.obterMetaVigenteCompetencia(listaMeta, cpeMes, cpeAno);
					if (meta != null) {
						vo.setQuantidadeTeto(meta.getQuantidade());
						vo.setValorTeto(meta.getValor());
						
						if (meta.getQuantidade() != null) {
							vo.setDiferencaQuantidadeTeto(meta.getQuantidade() - vo.getQuantidade().longValue());
						} else {
							vo.setDiferencaQuantidadeTeto(null);
						}
						
						if (meta.getValor() != null) {
							vo.setDiferencaValorTeto(meta.getValor().subtract(vo.getTotal()));
						} else {
							vo.setDiferencaValorTeto(null);	
						}	
					}
				}
				
				listaMeta = fatMetaDAO.listarMetaPeloGrupoPeloSubGrupoPelaFormOrgPeloFinancPeloProced(vo.getGrupo(), vo.getSubGrupo(), vo.getFogCod(), vo.getFcfSeq(), null, null);
				if (!listaMeta.isEmpty()) {
					FatMeta meta = faturamentoAmbulatorioON.obterMetaVigenteCompetencia(listaMeta, cpeMes, cpeAno);
					if (meta != null) {
						vo.setQuantidadeTeto(meta.getQuantidade());
						vo.setValorTeto(meta.getValor());
						
						if (meta.getQuantidade() != null) {
							vo.setDiferencaQuantidadeTeto(meta.getQuantidade() - vo.getQuantidade().longValue());
						} else {
							vo.setDiferencaQuantidadeTeto(null);
						}
						
						if (meta.getValor() != null) {
							vo.setDiferencaValorTeto(meta.getValor().subtract(vo.getTotal()));
						} else {
							vo.setDiferencaValorTeto(null);	
						}	
					}
				}
			}
		}
	}
	
	protected FatProcedAmbRealizadoDAO getFatProcedAmbRealizadoDAO() {
		return fatProcedAmbRealizadoDAO;
	}
	
	protected FatMetaDAO getFatMetaDAO() {
		return fatMetaDAO;
	}
	
	protected FaturamentoAmbulatorioON getFaturamentoAmbulatorioON() {
		return faturamentoAmbulatorioON;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	public IInternacaoFacade getInternacaoFacade() {
		return internacaoFacade;
	}

	
}
