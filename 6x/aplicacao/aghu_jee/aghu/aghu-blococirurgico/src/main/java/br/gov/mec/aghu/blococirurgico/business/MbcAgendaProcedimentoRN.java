package br.gov.mec.aghu.blococirurgico.business;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.blococirurgico.dao.MbcAgendaProcedimentoDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcAgendasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcEquipamentoCirgPorUnidDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProcEspPorCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProcedimentoPorGrupoDAO;
import br.gov.mec.aghu.dominio.DominioOperacaoAgenda;
import br.gov.mec.aghu.dominio.DominioOrigem;
import br.gov.mec.aghu.dominio.DominioSituacaoAgendas;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.MbcAgendaProcedimento;
import br.gov.mec.aghu.model.MbcAgendaProcedimentoId;
import br.gov.mec.aghu.model.MbcAgendas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * Classe responsável por prover os métodos de negócio de MbcAgendaProcedimento.
 * 
 * @autor fwinck
 * 
 */
@Stateless
public class MbcAgendaProcedimentoRN extends BaseBusiness {
@EJB
private IServidorLogadoFacade servidorLogadoFacade;

	private static final Log LOG = LogFactory.getLog(MbcAgendaProcedimentoRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcAgendasDAO mbcAgendasDAO;

	@Inject
	private MbcEquipamentoCirgPorUnidDAO mbcEquipamentoCirgPorUnidDAO;

	@Inject
	private MbcProcEspPorCirurgiasDAO mbcProcEspPorCirurgiasDAO;

	@Inject
	private MbcAgendaProcedimentoDAO mbcAgendaProcedimentoDAO;

	@Inject
	private MbcProcedimentoPorGrupoDAO mbcProcedimentoPorGrupoDAO;


	@EJB
	private MbcAgendasRN mbcAgendasRN;

	@EJB
	private MbcAgendaHistoricoRN mbcAgendaHistoricoRN;

	@EJB
	private IParametroFacade iParametroFacade;

	private static final long serialVersionUID = -3458622371469364380L;
	
	public enum MbcAgendaProcedimentoRNExceptionCode implements BusinessExceptionCode {
		MBC_00833, MBC_00862, MBC_00858, MBC_00864, MBC_01069, MBC_00891, MBC_00974;
	}

	public void persistirAgendaProcedimentos(MbcAgendaProcedimento oldAgendaProcedimento, MbcAgendaProcedimento agendaProcedimento) throws BaseException {
		if (agendaProcedimento.getId() == null) {
			definirId(agendaProcedimento);
			preInserir(agendaProcedimento);
			this.getMbcAgendaProcedimentoDAO().persistir(agendaProcedimento);
		} else {
			preAtualizar(agendaProcedimento, oldAgendaProcedimento);
			this.getMbcAgendaProcedimentoDAO().merge(agendaProcedimento);
		}
	}

	private void definirId(MbcAgendaProcedimento agendaProc){
		MbcAgendaProcedimentoId id = new MbcAgendaProcedimentoId();
		id.setAgdSeq(agendaProc.getMbcAgendas().getSeq());
		id.setEprEspSeq(agendaProc.getMbcEspecialidadeProcCirgs().getId().getEspSeq());
		id.setEprPciSeq(agendaProc.getMbcEspecialidadeProcCirgs().getId().getPciSeq());
		agendaProc.setId(id);
	}
	
	public void deletar(MbcAgendaProcedimento agendaProcedimento) throws BaseException {
		preDeletar(agendaProcedimento);
		this.getMbcAgendaProcedimentoDAO().remover(agendaProcedimento);
	}
	
	/**
	 * @ORADB MBCT_AGT_BRI
	 * 
	 * 
	 * @throws ApplicationBusinessException 
	 */
	private void preInserir(MbcAgendaProcedimento mbcAgendaProcedimento) throws BaseException {
RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		mbcAgendaProcedimento.setCriadoEm(new Date());
		mbcAgendaProcedimento.setRapServidores(servidorLogado);
		validarAgendaComControleEscalaCirurgicaDefinitiva(mbcAgendaProcedimento.getMbcAgendas());
		verificarDisponibilideDeEquipamentos(mbcAgendaProcedimento.getId().getEprPciSeq(),
				mbcAgendaProcedimento.getMbcAgendas().getIndSituacao(), mbcAgendaProcedimento.getMbcAgendas().getSeq(),
				mbcAgendaProcedimento.getMbcAgendas().getIndGeradoSistema(), mbcAgendaProcedimento.getMbcAgendas().getDthrPrevInicio(),
				mbcAgendaProcedimento.getMbcAgendas().getDthrPrevFim(), 
				mbcAgendaProcedimento.getMbcAgendas().getUnidadeFuncional().getSeq(),
				mbcAgendaProcedimento.getMbcAgendas().getDtAgenda(), "rn_agtp_ver_utlz_equ");
		verificarQuantidadeInformada(mbcAgendaProcedimento);
		verificarProcedimentoPrincipal(mbcAgendaProcedimento);
	}
	
	private void preAtualizar(MbcAgendaProcedimento newAgendaProcedimento, MbcAgendaProcedimento oldAgendaProcedimento
			) throws BaseException {
		validarAgendaComControleEscalaCirurgicaDefinitiva(newAgendaProcedimento.getMbcAgendas());
		if(!newAgendaProcedimento.getQtde().equals(oldAgendaProcedimento.getQtde())
				|| !newAgendaProcedimento.getId().getEprPciSeq().equals(oldAgendaProcedimento.getId().getEprPciSeq())) {
			verificarQuantidadeInformada(newAgendaProcedimento);
		}
		verificarProcedimentoPrincipal(newAgendaProcedimento);
		if(!oldAgendaProcedimento.getId().getEprPciSeq().equals(newAgendaProcedimento.getId().getEprPciSeq())
				|| !oldAgendaProcedimento.getId().getEprPciSeq().equals(newAgendaProcedimento.getId().getEprPciSeq())
				|| !oldAgendaProcedimento.getQtde().equals(newAgendaProcedimento.getQtde())) {
			popularSalvarHistoricoAgenda(newAgendaProcedimento, oldAgendaProcedimento);
		}
	}
	
	private void preDeletar(MbcAgendaProcedimento agendaProcedimento) throws BaseException {
		validarAgendaComControleEscalaCirurgicaDefinitiva(agendaProcedimento.getMbcAgendas());
		popularSalvarHistoricoAgenda(agendaProcedimento, null);
	}
	
	/**
	 * @ORADB mbck_agt_rn.rn_agtp_ver_escala
	 * 
	 * 
	 * @throws ApplicationBusinessException 
	 */
	public void validarAgendaComControleEscalaCirurgicaDefinitiva(MbcAgendas agenda) throws ApplicationBusinessException {
		MbcAgendas result = getMbcAgendasDAO().pesquisarAgendaComControleEscalaCirurgicaDefinitiva(agenda.getSeq());
		if(result != null && !result.getIndGeradoSistema()) {
			throw new ApplicationBusinessException(MbcAgendaProcedimentoRNExceptionCode.MBC_00833);
		}
	}
	
	/**rn_agdp_ver_utlz_equ
	 * @ORADB mbck_agd_rn.rn_agdp_ver_utlz_equ
	 * @ORADB mbck_agt_rn.rn_agtp_ver_utlz_equ
	 * @param pciSeq
	 * @param indSituacao
	 * @param agdSeq
	 * @param indGeradoSistema
	 * @param dthrPrevInicio
	 * @param dthrPrevFim
	 * @param unfSeq
	 * @param dtAgenda
	 * @throws BaseException
	 */
	public void verificarDisponibilideDeEquipamentos(
			Integer pciSeq, DominioSituacaoAgendas indSituacao, Integer agdSeq,
			Boolean indGeradoSistema, Date dthrPrevInicio, Date dthrPrevFim,
			Short unfSeq, Date dtAgenda, String procedureChamada) throws BaseException {
		if(!indGeradoSistema && dthrPrevInicio != null) {
			//Verifica se procedimento tem como responsável o agendamento
			if((DominioSituacaoAgendas.ES.equals(indSituacao) && "rn_agdp_ver_utlz_equ".equals(procedureChamada))
					|| "rn_agtp_ver_utlz_equ".equals(procedureChamada)) {
				AghParametros grupoVideo = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_GRUPO_VIDEOLAPAROSCOPIA);
				BigDecimal grupoVideoNum = grupoVideo.getVlrNumerico();
				
				AghParametros equipamentoVideo = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_EQUIPAMENTO_VIDEOLAP);
				BigDecimal equipamentoVideoNum = equipamentoVideo.getVlrNumerico();
				
				//Busca grupo do procedimento e verifica se é igual o do parâmetro
				List<Short> gruposSeq = getMbcProcedimentoPorGrupoDAO().listarGrupoSeqPorProcedimento(pciSeq);
				if(gruposSeq.contains(grupoVideoNum.shortValue())) {
					//Busca quantidade de equipamentos da unidade
					Short quantidade = getMbcEquipamentoCirgPorUnidDAO().obterQuantidadePorId(equipamentoVideoNum.shortValue(),
							unfSeq);
					if(quantidade != null) {
						//Verifica se a quantidade de cirurgias com o procedimento é
						//maior que o número de equipamentos disponíveis na unidade
						Integer qtdeCirgComDetProc = getMbcProcEspPorCirurgiasDAO().buscarQuantidadeCirurgiasComDetProc(
								agdSeq, unfSeq, dtAgenda, grupoVideoNum.shortValue());
						if(qtdeCirgComDetProc >= quantidade) {
							//Verifica colisão de horários das cirurgias com o procedimento
							List<MbcAgendas> listaHorariosColisao = getMbcAgendasDAO().buscarAgendasComProcEquipColisaoHorario(
									dtAgenda, unfSeq, agdSeq, grupoVideoNum.shortValue());
							Integer quantidadeConflitos = 0;
							
							for(MbcAgendas item : listaHorariosColisao) {
								if(!(item.getDthrPrevInicio().compareTo(dthrPrevInicio) < 0
										&& item.getDthrPrevFim().compareTo(dthrPrevInicio) < 0)) {
									if(!(item.getDthrPrevInicio().compareTo(dthrPrevFim) >= 0)) {
										quantidadeConflitos++;
									}
								}
							}
							if(quantidadeConflitos >= quantidade) {
								if("rn_agtp_ver_utlz_equ".equals(procedureChamada)) {
									throw new ApplicationBusinessException(MbcAgendaProcedimentoRNExceptionCode.MBC_00862);
								} else {
									throw new ApplicationBusinessException(MbcAgendaProcedimentoRNExceptionCode.MBC_00858);
								}
							}
							
						}
					}
				}
			}
		}
	}
	
	/**
	 * @ORADB mbck_agt_rn.rn_agtp_ver_quant
	 * 
	 * @param mbcAgendaProcedimento
	 * @throws BaseException
	 */
	public void verificarQuantidadeInformada(MbcAgendaProcedimento mbcAgendaProcedimento) throws ApplicationBusinessException {
		if(mbcAgendaProcedimento.getQtde() <= 0) {
			throw new ApplicationBusinessException(MbcAgendaProcedimentoRNExceptionCode.MBC_00974,
					mbcAgendaProcedimento.getProcedimentoCirurgico().getDescricao());
		}
		if(!mbcAgendaProcedimento.getMbcAgendas().getIndGeradoSistema()) {
			if(mbcAgendaProcedimento.getQtde() > 1 
					&& !mbcAgendaProcedimento.getProcedimentoCirurgico().getIndProcMultiplo()) {
				throw new ApplicationBusinessException(MbcAgendaProcedimentoRNExceptionCode.MBC_00864);
			}
			SimpleDateFormat formatter = new SimpleDateFormat("HHmm");
			String dataformatada = formatter.format(mbcAgendaProcedimento.getMbcAgendas().getTempoSala());
			Short tempoSala = Short.valueOf(dataformatada);
			
			if(tempoSala < mbcAgendaProcedimento.getProcedimentoCirurgico().getTempoMinimo()) {
				throw new ApplicationBusinessException(MbcAgendaProcedimentoRNExceptionCode.MBC_01069,
						getMbcAgendasRN().gerarTempoMinimoEditado(mbcAgendaProcedimento),
						mbcAgendaProcedimento.getProcedimentoCirurgico().getDescricao());
			}
		}
	}
	
	/**
	 * @ORADB mbck_agt_rn.rn_agtp_ver_ind_prin
	 * 
	 * @param mbcAgendaProcedimento
	 * @throws BaseException
	 */
	public void verificarProcedimentoPrincipal(MbcAgendaProcedimento mbcAgendaProcedimento) throws ApplicationBusinessException {
		if(mbcAgendaProcedimento.getMbcAgendas().getEspProcCirgs().getId().getEspSeq().equals(mbcAgendaProcedimento.getId().getEprEspSeq())
				&& mbcAgendaProcedimento.getMbcAgendas().getEspProcCirgs().getId().getPciSeq().equals(mbcAgendaProcedimento.getId().getEprPciSeq())
				&& !mbcAgendaProcedimento.getMbcAgendas().getIndGeradoSistema()) {
			throw new ApplicationBusinessException(MbcAgendaProcedimentoRNExceptionCode.MBC_00891);
		}
	}
	
	/**
	 * @ORADB mbck_agt_rn.rn_agtp_inc_historic
	 * @param newAgendaProcedimento
	 * @param oldAgendaProcedimento
	 * @throws BaseException 
	 */
	private void popularSalvarHistoricoAgenda(MbcAgendaProcedimento newAgendaProcedimento, MbcAgendaProcedimento oldAgendaProcedimento) throws BaseException {
		StringBuilder descricao = new StringBuilder();
		DominioOperacaoAgenda operacao;
		
		if(oldAgendaProcedimento != null) {
			operacao = DominioOperacaoAgenda.A;
			//procedimentos
			if(!oldAgendaProcedimento.getId().getEprPciSeq().equals(newAgendaProcedimento.getId().getEprPciSeq())) {
				descricao.append("Outro procedimento alterado de ")
					.append(oldAgendaProcedimento.getProcedimentoCirurgico().getDescricao())
					.append(" para ").append(newAgendaProcedimento.getProcedimentoCirurgico().getDescricao());
				//quantidade modificada e outro procedimento modificado
				if(!oldAgendaProcedimento.getQtde().equals(newAgendaProcedimento.getQtde())) {
					descricao.append(", ");
					if(oldAgendaProcedimento.getQtde() == null && newAgendaProcedimento.getQtde() != null) {
						descricao.append("Quantidade incluída: ").append(newAgendaProcedimento.getQtde().toString());
					} else if(oldAgendaProcedimento.getQtde() != null && newAgendaProcedimento.getQtde() != null) {
						descricao.append("Quantidade alterada de ").append(oldAgendaProcedimento.getQtde().toString())
						.append(" para ").append(newAgendaProcedimento.getQtde().toString());
					} else if(oldAgendaProcedimento.getQtde() != null && newAgendaProcedimento.getQtde() == null) {
						descricao.append("Quantidade excluída: ").append(oldAgendaProcedimento.getQtde().toString());
					}
				}
			//quantidade modificada e outro procedimento não modificado
			} else if(!oldAgendaProcedimento.getQtde().equals(newAgendaProcedimento.getQtde())){
				if(oldAgendaProcedimento.getQtde() == null && newAgendaProcedimento.getQtde() != null) {
					descricao.append("Quantidade incluída para outro procedimento ")
						.append(newAgendaProcedimento.getProcedimentoCirurgico().getDescricao()).append(": ")
						.append(newAgendaProcedimento.getQtde().toString());
				} else if(oldAgendaProcedimento.getQtde() != null && newAgendaProcedimento.getQtde() != null) {
					descricao.append("Quantidade do outro procedimento ")
						.append(newAgendaProcedimento.getProcedimentoCirurgico().getDescricao()).append(" alterada de ")
						.append(oldAgendaProcedimento.getQtde().toString()).append(" para ")
						.append(newAgendaProcedimento.getQtde().toString());
				} else if(oldAgendaProcedimento.getQtde() != null && newAgendaProcedimento.getQtde() == null) {
					descricao.append("Quantidade excluída do outro procedimento ")
						.append(oldAgendaProcedimento.getProcedimentoCirurgico().getDescricao()).append(": ")
						.append(oldAgendaProcedimento.getQtde().toString());
				}
			}
		} else {
			operacao = DominioOperacaoAgenda.E;
			descricao.append("outro procedimento excluído: ").append(newAgendaProcedimento.getProcedimentoCirurgico().getDescricao())
					.append(" quantidade: ").append(newAgendaProcedimento.getQtde());
		}
		getMbcAgendaHistoricoRN().inserir(newAgendaProcedimento.getMbcAgendas().getSeq(),
				newAgendaProcedimento.getMbcAgendas().getIndSituacao(),
				DominioOrigem.P, descricao.toString(), operacao);
	}
	
	protected MbcProcedimentoPorGrupoDAO getMbcProcedimentoPorGrupoDAO() {
		return mbcProcedimentoPorGrupoDAO;
	}
	
	protected MbcAgendaHistoricoRN getMbcAgendaHistoricoRN() {
		return mbcAgendaHistoricoRN;
	}
	
	protected MbcAgendasRN getMbcAgendasRN() {
		return mbcAgendasRN;
	}
	
	protected MbcProcEspPorCirurgiasDAO getMbcProcEspPorCirurgiasDAO() {
		return mbcProcEspPorCirurgiasDAO;
	}
	
	protected MbcAgendaProcedimentoDAO getMbcAgendaProcedimentoDAO() {
		return mbcAgendaProcedimentoDAO;
	}
	
	protected MbcAgendasDAO getMbcAgendasDAO() {
		return mbcAgendasDAO;
	}
	
	protected MbcEquipamentoCirgPorUnidDAO getMbcEquipamentoCirgPorUnidDAO() {
		return mbcEquipamentoCirgPorUnidDAO;
	}
	
	protected IParametroFacade getParametroFacade() {
		return iParametroFacade;
	}
	
}