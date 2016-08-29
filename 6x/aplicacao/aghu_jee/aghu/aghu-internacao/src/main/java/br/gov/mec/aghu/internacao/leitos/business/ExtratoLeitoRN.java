package br.gov.mec.aghu.internacao.leitos.business;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.controleinfeccao.business.IControleInfeccaoFacade;
import br.gov.mec.aghu.dao.AghWork;
import br.gov.mec.aghu.dao.EsquemasOracleEnum;
import br.gov.mec.aghu.dao.ObjetosBancoOracleEnum;
import br.gov.mec.aghu.dominio.DominioMovimentoLeito;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.internacao.dao.AinExtratoLeitosDAO;
import br.gov.mec.aghu.internacao.dao.AinLeitosDAO;
import br.gov.mec.aghu.internacao.leitos.exception.AGHULeitoOutofDateException;
import br.gov.mec.aghu.internacao.leitos.exception.AGHULetioOptimistLockException;
import br.gov.mec.aghu.model.AghCaractUnidFuncionais;
import br.gov.mec.aghu.model.AghOrigemEventos;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AinAtendimentosUrgencia;
import br.gov.mec.aghu.model.AinExtratoLeitos;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AinTiposMovimentoLeito;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseOptimisticLockException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * ORADB PROCEDURE AINP_ENFORCE_EXL_RULES
 * 
 * @author lalegre
 * 
 */
@Stateless
public class ExtratoLeitoRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(ExtratoLeitoRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IInternacaoFacade internacaoFacade;
	
	@EJB
	private IControleInfeccaoFacade controleInfeccaoFacade;
	
	@Inject
	private AinExtratoLeitosDAO ainExtratoLeitosDAO;
	
	@Inject
	private AinLeitosDAO ainLeitosDAO;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = -6241303614386642861L;

	/**
	 * Enumeracao com os codigos de mensagens de excecoes negociais.
	 * 
	 * Cada entrada nesta enumeracao deve corresponder a um chave no message
	 * bundle.
	 * 
	 * Porém se não houver uma este valor será enviado como mensagem de execeção
	 * sem localização alguma.
	 */
	private enum ExtratoLeitoRNExceptionCode implements BusinessExceptionCode {
		AIN_00417, AIN_00426, AIN_00194, AIN_00184, ERRO_EXTRATO, ERRO_ATUALIZAR_QUARTO
		, ERRO_GERAL_ATUALIZACAO_LEITO
		, OPTIMISTIC_LOCK_LEITO
	}
			
	/**
	 * Atualiza Leito gerando um novo extrato
	 * ORADB AINT_EXL_BRI
	 * ORADB AINP_ENFORCE_EXL_RULES
	 * 
	 * @param leito
	 * @param tiposMovimentoLeito
	 * @param rapServidor
	 * @param rapServidorResponsavel
	 * @param justificativa
	 * @param dataLancamento
	 * @param paciente
	 * @param internacao
	 * @param tempoReserva
	 * @param atendimentoUrgencia
	 * @param origemEventos
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public void inserirExtrato(final AinLeitos leito,
			AinTiposMovimentoLeito tiposMovimentoLeito,
			RapServidores rapServidor, RapServidores rapServidorResponsavel, String justificativa,
			Date dataLancamento, Date criadoEm, AipPacientes paciente,
			AinInternacao internacao, Short tempoReserva,
			AinAtendimentosUrgencia atendimentoUrgencia,
			AghOrigemEventos origemEventos) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		if (criadoEm == null) {
			criadoEm = new Date();
		}
		if (dataLancamento == null) {
			dataLancamento = new Date();
		}
		if (rapServidor == null) {			
			rapServidor = servidorLogado;
		}

		/* VERIFICA SE A DATA DO EXTRATO É MAIOR QUE O ÚLTIMO EXTRATO GERADO
		 * PARA ESTE LEITO
		 */
		Short seqUnidFuncional = leito == null
				|| leito.getUnidadeFuncional() == null ? null : leito
				.getUnidadeFuncional().getSeq();
		if (leito.getLeitoID() != null
				&& verificarCaracteristicaUnidadeFuncional(seqUnidFuncional,
						ConstanteAghCaractUnidFuncionais.UNID_INTERNACAO)) {
			if (dataLancamento.after(new Date())) {
				throw new ApplicationBusinessException(ExtratoLeitoRNExceptionCode.AIN_00417);
			} else {
				verificarDataExtratoLeito(leito.getLeitoID(), dataLancamento);
			}
		}

		// Verifica se necessita de limpeza
		if (verificarLimpeza(leito, tiposMovimentoLeito)) {
			tiposMovimentoLeito = getCadastrosBasicosInternacaoFacade()
					.pesquisarTipoSituacaoBloqueioLimpezaPorDescricao("Limpeza");
		}

		// Atualiza o Leito
		atualizarLeito(leito, tiposMovimentoLeito, internacao);

		// Gera Extrato
		gerarNovoExtratoLeito(leito, tiposMovimentoLeito, rapServidor,
				rapServidorResponsavel, justificativa, dataLancamento, criadoEm, paciente, internacao,
				tempoReserva, atendimentoUrgencia, origemEventos);

		// Verifica se necessita atualizar sexo quarto
		if (tiposMovimentoLeito != null
				&& tiposMovimentoLeito.getGrupoMvtoLeito() == DominioMovimentoLeito.L) {
			try {
				getInternacaoFacade().atualizarSexoQuarto(leito.getLeitoID(), null);	
			} catch (Exception e) {
				logError(e);
				throw new ApplicationBusinessException(
						ExtratoLeitoRNExceptionCode.ERRO_ATUALIZAR_QUARTO);
			}
		}
		
		if (getAinExtratoLeitosDAO().isOracle()) {
			if (getControleInfeccaoFacade().verificaLeitoControladoCCIH(leito.getLeitoID()) && internacao != null) {
				final Integer intSeq = Integer.valueOf(internacao.getSeq());
				if (DominioMovimentoLeito.O.equals(tiposMovimentoLeito.getGrupoMvtoLeito())) {
					// Leito está sendo ocupado, insere controle
					AghWork work = new AghWork(servidorLogado != null ? servidorLogado.getUsuario() : null) {
						public void executeAghProcedure(Connection connection) throws SQLException {
							
							CallableStatement cs = null;
							try {
								StringBuilder sbCall = new StringBuilder("{call ");
								sbCall.append(EsquemasOracleEnum.AGH.toString())
								.append('.')
								.append(ObjetosBancoOracleEnum.MCIK_LEITOS_MCIP_INSERE_CONTROLE.toString())
								.append("(?,?,?,?)}");
								
								cs = connection.prepareCall(sbCall.toString());
								cs.setInt(1, intSeq);
								cs.setString(2, leito.getLeitoID());
								cs.setShort(3, leito.getTipoMovimentoLeito().getCodigo());
								cs.setTimestamp(4, new java.sql.Timestamp(new Date().getTime()));
								
								cs.execute();
							} finally {
								if (cs != null) {
									cs.close();
								}
							}
						}
					};
					
					 ainExtratoLeitosDAO.doWork(work);					
					
					if (work.getException() != null){
						throw new HibernateException(work.getException());
					}
				} else if (DominioMovimentoLeito.O.equals(leito.getTipoMovimentoLeito().getGrupoMvtoLeito())) {
					// Se leito está sendo desocupado, atualiza controle
					AghWork work = new AghWork(servidorLogado != null ? servidorLogado.getUsuario() : null) {
						public void executeAghProcedure(Connection connection) throws SQLException {
							
							CallableStatement cs = null;
							try {
								StringBuilder sbCall = new StringBuilder("{call ");
								sbCall.append(EsquemasOracleEnum.AGH.toString())
								.append('.')
								.append(ObjetosBancoOracleEnum.MCIK_LEITOS_MCIP_ALTERA_CONTROLE.toString())
								.append("(?,?,?)}");
								
								cs = connection.prepareCall(sbCall.toString());
								cs.setString(1, leito.getLeitoID());
								cs.setShort(2, leito.getTipoMovimentoLeito().getCodigo());
								cs.setDate(3, new java.sql.Date(new Date().getTime()));
								
								cs.execute();
							} finally {
								if (cs != null) {
									cs.close();
								}
							}
						}
					};		
					ainExtratoLeitosDAO.doWork(work);
					if (work.getException() != null){
						throw new HibernateException(work.getException());
					}
				}
			}
		}
		
	}

	/**
	 * Método para verificar se uma unidade funcional tem uma determinada
	 * característica.
	 * 
	 * ORADB AGHC_VER_CARACT_UNF
	 * 
	 * @param seq Unidade Funcional
	 * @param caracteristica
	 * @return true/false
	 */
	public Boolean verificarCaracteristicaUnidadeFuncional(Short seq,
			ConstanteAghCaractUnidFuncionais caracteristica) {

		if (seq == null || caracteristica == null) {
			return false;
		} else {
			List<AghCaractUnidFuncionais> caracteristicaList = this
					.pesquisarCaracteristicaUnidadeFuncional(seq,
							caracteristica);

			return caracteristicaList.size() > 0;
		}
	}

	/**
	 * Método para fazer busca de unidade funcional pelo seu seq e sua
	 * característica.
	 * 
	 * @param seq
	 *            da unidade funcional
	 * @param caracteristica
	 * @return lista de unidades funcionais
	 */
	private List<AghCaractUnidFuncionais> pesquisarCaracteristicaUnidadeFuncional(Short seq,
			ConstanteAghCaractUnidFuncionais caracteristica) {
		return getAghuFacade().pesquisarCaracteristicaUnidadeFuncional(seq, caracteristica);
	}

	/**
	 * Valida a data de lançamento de acordo com o último extrato gerado
	 * 
	 * @param dataLancamento
	 */
	private void verificarDataExtratoLeito(String codigoLeito, Date dataLancamento) throws ApplicationBusinessException {
		List<AinExtratoLeitos> extratos = getAinExtratoLeitosDAO().pesquisarExtratoPorLeitoOrderDataHoraLanc(codigoLeito);
		if (extratos.size() > 0 && extratos.get(0) != null) {
			Date extratoDthrLancamento = extratos.get(0).getDthrLancamento();
			if (extratoDthrLancamento.after(dataLancamento)) {
				throw new AGHULeitoOutofDateException(ExtratoLeitoRNExceptionCode.AIN_00426, DateFormatUtils.format(
						extratoDthrLancamento, "dd/MM/yyyy HH:mm"));
			}
		}
	}

	/**
	 * Verifica se o leito necessita de limpeza
	 * 
	 * @param leito
	 */
	public boolean verificarLimpeza(AinLeitos leito,
			AinTiposMovimentoLeito tiposMovimentoLeito) {

		if (tiposMovimentoLeito != null
				&& tiposMovimentoLeito.getGrupoMvtoLeito() == DominioMovimentoLeito.L
				&& leito.getIndBloqLeitoLimpeza()
				&& leito.getTipoMovimentoLeito()
						.isIndNecessitaLimpezaCheckBox()
				&& (leito.getTipoMovimentoLeito().getGrupoMvtoLeito() == DominioMovimentoLeito.B
						|| leito.getTipoMovimentoLeito().getGrupoMvtoLeito() == DominioMovimentoLeito.BI || leito
						.getTipoMovimentoLeito().getGrupoMvtoLeito() == DominioMovimentoLeito.R)) {
			return true;
		}

		return false;
	}

	/**
	 * Atualiza leito
	 * 
	 * @param leito
	 * @param tiposMovimentoLeito
	 * @throws ApplicationBusinessException
	 */
	
	public void atualizarLeito(AinLeitos leito, AinTiposMovimentoLeito tiposMovimentoLeito, AinInternacao internacao)
			throws ApplicationBusinessException {
		
		if(leito != null && leito.getQuarto() != null && 
			leito.getQuarto().getNumero() != null && tiposMovimentoLeito != null){
			getCadastrosBasicosInternacaoFacade().validaLeitoMedidaPreventiva(leito.getQuarto().getNumero(), tiposMovimentoLeito.getCodigo());
		}

		try {
			if (leito != null) {
				leito.setTipoMovimentoLeito(tiposMovimentoLeito);

				AghParametros aghParametros = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_COD_MVTO_LTO_DESOCUPADO);

				if (aghParametros != null && leito.getTipoMovimentoLeito().getCodigo().equals(aghParametros.getVlrNumerico().shortValue())) {
					leito.setInternacao(null);
				} else {
					leito.setInternacao(internacao);
				}

				AinLeitosDAO ainLeitosDAO = this.getAinLeitosDAO();
				ainLeitosDAO.merge(leito);
				ainLeitosDAO.flush();
			}
		} catch (BaseOptimisticLockException e) {
			throw new AGHULetioOptimistLockException(ExtratoLeitoRNExceptionCode.OPTIMISTIC_LOCK_LEITO, leito.getLeitoID());
		} catch (Exception e) {
			this.logError(e.getMessage(), e);
			throw new ApplicationBusinessException(ExtratoLeitoRNExceptionCode.ERRO_GERAL_ATUALIZACAO_LEITO, leito.getLeitoID(),
					e.getMessage());
		}
	}
	
	/**
	 * Gera um novo extrato em AIN_EXTRATO_LEITOS
	 * 
	 * @param leito
	 * @param tiposMovimentoLeito
	 * @param rapServidor
	 * @param rapServidorResponsavel
	 * @param justificativa
	 * @param dataLancamento
	 * @param criadoEm
	 * @param paciente
	 * @param internacao
	 * @param tempoReserva
	 * @param atendimentoUrgencia
	 * @param origemEventos
	 * @throws ApplicationBusinessException
	 */
	
	public void gerarNovoExtratoLeito(AinLeitos leito,
			AinTiposMovimentoLeito tiposMovimentoLeito,
			RapServidores rapServidor, RapServidores rapServidorResponsavel, String justificativa,
			Date dataLancamento, Date criadoEm, AipPacientes paciente,
			AinInternacao internacao, Short tempoReserva,
			AinAtendimentosUrgencia atendimentoUrgencia,
			AghOrigemEventos origemEventos) throws ApplicationBusinessException {

		AinExtratoLeitos extrato = new AinExtratoLeitos();
		extrato.setAtendimentoUrgencia(atendimentoUrgencia);
		extrato.setCriadoEm(criadoEm);
		extrato.setDthrLancamento(dataLancamento);
		extrato.setInternacao(internacao);
		extrato.setJustificativa(justificativa);
		extrato.setLeito(leito);
		extrato.setOrigemEventos(origemEventos);
		extrato.setPaciente(paciente);
		extrato.setServidor(rapServidor);
		extrato.setServidorResponsavel(rapServidorResponsavel);
		extrato.setTempoReserva(tempoReserva);
		extrato.setTipoMovimentoLeito(tiposMovimentoLeito);
		
		try {
			AinExtratoLeitosDAO ainExtratoLeitosDAO = this.getAinExtratoLeitosDAO();
			ainExtratoLeitosDAO.persistir(extrato);
			ainExtratoLeitosDAO.flush();
		} catch (Exception e) {
			this.logError(e.getMessage());
			throw new ApplicationBusinessException(
					ExtratoLeitoRNExceptionCode.ERRO_EXTRATO);
		}
	}
	
	

	protected AinExtratoLeitosDAO getAinExtratoLeitosDAO() {
		return ainExtratoLeitosDAO;
	}

	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}
	
	protected AinLeitosDAO getAinLeitosDAO() {
		return ainLeitosDAO;
	}

	protected ICadastrosBasicosInternacaoFacade getCadastrosBasicosInternacaoFacade() {
		return this.cadastrosBasicosInternacaoFacade;
	}

	protected IControleInfeccaoFacade getControleInfeccaoFacade() {
		return controleInfeccaoFacade;
	}

	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	protected IInternacaoFacade getInternacaoFacade() {
		return internacaoFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
