package br.gov.mec.aghu.transplante.business;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.dao.MamDiagnosticoDAO;
import br.gov.mec.aghu.configuracao.dao.AghAtendimentoDAO;
import br.gov.mec.aghu.controleinfeccao.dao.MciNotificacaoGmrDAO;
import br.gov.mec.aghu.dominio.DominioIndOrigemItemContaHospitalar;
import br.gov.mec.aghu.dominio.DominioItemConsultoriaSumarios;
import br.gov.mec.aghu.dominio.DominioLocalCobranca;
import br.gov.mec.aghu.dominio.DominioSituacaoItenConta;
import br.gov.mec.aghu.dominio.DominioSituacaoTmo;
import br.gov.mec.aghu.dominio.DominioSituacaoTransplante;
import br.gov.mec.aghu.dominio.DominioTipoAlogenico;
import br.gov.mec.aghu.exames.dao.AelResultadoExameDAO;
import br.gov.mec.aghu.faturamento.dao.FatItemContaHospitalarDAO;
import br.gov.mec.aghu.faturamento.dao.FatProcedHospInternosDAO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.AipRegSanguineos;
import br.gov.mec.aghu.model.AipRegSanguineosId;
import br.gov.mec.aghu.model.FatContasHospitalares;
import br.gov.mec.aghu.model.FatItemContaHospitalar;
import br.gov.mec.aghu.model.FatItemContaHospitalarId;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.model.MtxContatoPacientes;
import br.gov.mec.aghu.model.MtxCriterioPriorizacaoTmo;
import br.gov.mec.aghu.model.MtxExtratoTransplantes;
import br.gov.mec.aghu.model.MtxMotivoAlteraSituacao;
import br.gov.mec.aghu.model.MtxProcedimentoTransplantes;
import br.gov.mec.aghu.model.MtxTransplantes;
import br.gov.mec.aghu.model.MtxTransplantesJn;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.dao.AipPacientesDAO;
import br.gov.mec.aghu.paciente.dao.AipRegSanguineosDAO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.transplante.dao.MtxContatoPacientesDAO;
import br.gov.mec.aghu.transplante.dao.MtxCriterioPriorizacaoTmoDAO;
import br.gov.mec.aghu.transplante.dao.MtxExtratoTransplantesDAO;
import br.gov.mec.aghu.transplante.dao.MtxMotivoAlteraSituacaoDAO;
import br.gov.mec.aghu.transplante.dao.MtxProcedimentoTransplantesDAO;
import br.gov.mec.aghu.transplante.dao.MtxTransplantesDAO;
import br.gov.mec.aghu.transplante.dao.MtxTransplantesJnDAO;
import br.gov.mec.aghu.transplante.vo.FiltroTransplanteOrgaoVO;
import br.gov.mec.aghu.transplante.vo.ListarTransplantesVO;
import br.gov.mec.aghu.transplante.vo.PacienteAguardandoTransplanteOrgaoVO;
import br.gov.mec.aghu.transplante.vo.PacienteTransplantadosOrgaoVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class MtxTransplantesRN extends BaseBusiness {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7432566291916651033L;
	
	private static final Log LOG = LogFactory.getLog(MtxTransplantesRN.class);
	
	private static final Integer UM = 1;
	private static final Integer DOZE = 12;
	private static final String ESPACO = " ";
	private static final String ANO = "ano";
	private static final String ANOS = "anos";
	private static final String MES = " mês";
	private static final String MESES = "meses";
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@Inject
	private MtxTransplantesDAO mtxTransplantesDAO;
	
	@Inject
	private MtxTransplantesJnDAO mtxTransplantesJnDAO;
	
	@Inject
	private MtxContatoPacientesDAO mtxContatoPacientesDAO;
	
	@Inject
	private AipRegSanguineosDAO aipRegSanguineosDAO;
		
	@Inject
	private MtxCriterioPriorizacaoTmoDAO mtxCriterioPriorizacaoTmoDAO;
	
	@Inject
	private MtxMotivoAlteraSituacaoDAO mtxMotivoAlteraSituacaoDAO;
	@Inject
	private MtxExtratoTransplantesDAO mtxExtratoTransplantesDAO;
	
	@Inject
	private MciNotificacaoGmrDAO mciNotificacaoGmrDAO;
	
	@Inject
	private MamDiagnosticoDAO mamDiagnosticoDAO;
	
	@Inject
	private AelResultadoExameDAO aelResultadoExameDAO;
	
	@Inject
	private AghAtendimentoDAO aghAtendimentoDAO;
	
	@Inject
	private FatItemContaHospitalarDAO fatItemContaHospitalarDAO;
	
	@Inject
	private FatProcedHospInternosDAO fatProcedHospInternosDAO;
	
	@Inject
	private AipPacientesDAO pacientesDAO;
	
	@Inject 
	private MtxProcedimentoTransplantesDAO mtxProcedimentoTransplantesDAO;
	
	@Override
	protected Log getLogger() {
		return LOG;
	}
	
	private enum MtxTransplantesExceptionCode implements BusinessExceptionCode {
		ERRO_INGRESSO_MENOR_NASCIMENTO,
		ERRO_DIALISE_MENOR_NASCIMENTO,
		ERRO_FISTULA_MENOR_NASCIMENTO,
		ERRO_TRANSFUSAO_MENOR_NASCIMENTO,
		ERRO_GESTACAO_MENOR_NASCIMENTO,
		ERRO_TRANSPLANTE_MENOR_NASCIMENTO;
	}

	/**
	 * Executa os requisitos funcionais da estoria #41796, 
	 * antes de persistir os dados da entidade {@link MtxTransplantes} 
	 * e dos Contatos do Paciente {@link MtxContatoPacientes}
	 * 
	 * @param transplante {@link MtxTransplantes}
	 * @param regSanguineo {@link AipRegSanguineos}
	 * @param paciente {@link AipPacientes}
	 * @param listaContatosIncluidos {@link List} de {@link MtxContatoPacientes}
	 * @param listaContatosExcluidos {@link List} de {@link MtxContatoPacientes}
	 * @throws ApplicationBusinessException
	 * @throws BaseListException 
	 */
	public void salvarTransplanteListaContatoPaciente(
			MtxTransplantes transplante, AipRegSanguineos regSanguineo, AipPacientes paciente, 
			List<MtxContatoPacientes> listaContatosIncluidos, List<MtxContatoPacientes> listaContatosExcluidos) 
			throws ApplicationBusinessException, BaseListException {

		this.validarDatasTransplante(transplante, paciente);
		
		RapServidores servidor = this.registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado());
		
		// RN1
		if (transplante.getSeq() == null) {
			transplante.setServidor(servidor);
			transplante.setCriadoEm(new Date());
			transplante.setSituacao(DominioSituacaoTransplante.E);
		} 
		
		// RN5
		this.gravarContatosPaciente(listaContatosIncluidos, paciente);
		this.removerContatosPaciente(listaContatosExcluidos);
		// RN3
		this.gravarRegistroSanguineo(regSanguineo, paciente, servidor);
		// Gravar entidade
		this.gravar(transplante, paciente);
	}

	/**
	 * Valida se as datas informadas para o transplante são maiores que a data de nascimento do paciente.
	 * 
	 * @param transplante {@link MtxTransplantes}
	 * @param paciente {@link AipPacientes}
	 * @throws BaseListException 
	 */
	private void validarDatasTransplante(MtxTransplantes transplante, AipPacientes paciente) throws BaseListException {
		
		BaseListException lista = new BaseListException();
		
		if (transplante != null && paciente != null && paciente.getDtNascimento() != null) {
			lista.add(validarDataIngresso(transplante, paciente));
			lista.add(validarDataDialise(transplante, paciente));
			lista.add(validarDataFistula(transplante, paciente));
			lista.add(validarDataUltimaTransfusao(transplante, paciente));
			lista.add(validarDataUltimaGestacao(transplante, paciente));
			lista.add(validarDataUltimoTransplante(transplante, paciente));
		}
		
		if (lista.hasException()) {
			throw lista;
		}
	}

	private ApplicationBusinessException validarDataUltimoTransplante(MtxTransplantes transplante, AipPacientes paciente) {
		if (transplante.getDataUltimoTransplante() != null && DateUtil.validaDataMenor(transplante.getDataUltimoTransplante(), paciente.getDtNascimento())) {
			return new ApplicationBusinessException(MtxTransplantesExceptionCode.ERRO_TRANSPLANTE_MENOR_NASCIMENTO);
		}
		return null;
	}

	private ApplicationBusinessException validarDataUltimaGestacao(MtxTransplantes transplante, AipPacientes paciente) {
		if (transplante.getDataUltimaGestacao() != null && DateUtil.validaDataMenor(transplante.getDataUltimaGestacao(), paciente.getDtNascimento())) {
			return new ApplicationBusinessException(MtxTransplantesExceptionCode.ERRO_GESTACAO_MENOR_NASCIMENTO);
		}
		return null;
	}

	private ApplicationBusinessException validarDataUltimaTransfusao(MtxTransplantes transplante, AipPacientes paciente) {
		if (transplante.getDataUltimaTransfusao() != null && DateUtil.validaDataMenor(transplante.getDataUltimaTransfusao(), paciente.getDtNascimento())) {
			return new ApplicationBusinessException(MtxTransplantesExceptionCode.ERRO_TRANSFUSAO_MENOR_NASCIMENTO);
		}
		return null;
	}

	private ApplicationBusinessException validarDataFistula(MtxTransplantes transplante, AipPacientes paciente) {
		if (transplante.getDataFistula() != null && DateUtil.validaDataMenor(transplante.getDataFistula(), paciente.getDtNascimento())) {
			return new ApplicationBusinessException(MtxTransplantesExceptionCode.ERRO_FISTULA_MENOR_NASCIMENTO);
		}
		return null;
	}

	private ApplicationBusinessException validarDataDialise(MtxTransplantes transplante, AipPacientes paciente) {
		if (transplante.getDataDialise() != null && DateUtil.validaDataMenor(transplante.getDataDialise(), paciente.getDtNascimento())) {
			return new ApplicationBusinessException(MtxTransplantesExceptionCode.ERRO_DIALISE_MENOR_NASCIMENTO);
		}
		return null;
	}

	private ApplicationBusinessException validarDataIngresso(MtxTransplantes transplante, AipPacientes paciente) {
		if (transplante.getDataIngresso() != null && DateUtil.validaDataMenor(transplante.getDataIngresso(), paciente.getDtNascimento())) {
			return new ApplicationBusinessException(MtxTransplantesExceptionCode.ERRO_INGRESSO_MENOR_NASCIMENTO);
		}
		return null;
	}
	
	/**
	 * Persiste novo registro de {@link AipRegSanguineos}.
	 * 
	 * @param regSanguineos {@link AipRegSanguineos}
	 * @param paciente {@link AipPacientes}
	 * @param servidor {@link RapServidores}
	 */
	private void gravarRegistroSanguineo(AipRegSanguineos regSanguineos, AipPacientes paciente, RapServidores servidor) {
		
		if (regSanguineos != null 
				&& servidor != null && servidor.getId() != null
				&& paciente != null && paciente.getCodigo() != null) {
			
			AipRegSanguineos novo = new AipRegSanguineos();
			novo.setId(new AipRegSanguineosId());
			novo.getId().setPacCodigo(paciente.getCodigo());
			novo.getId().setSeqp(this.obterProximoValorSequence(regSanguineos));
			novo.getId().setSerMatricula(servidor.getId().getMatricula());
			novo.getId().setSerVinCodigo(servidor.getId().getVinCodigo());
			novo.setGrupoSanguineo(regSanguineos.getGrupoSanguineo());
			novo.setFatorRh(regSanguineos.getFatorRh());
			novo.setCriadoEm(new Date());
			
			this.aipRegSanguineosDAO.persistir(novo);
		}
	}

	/**
	 * Obtem valor seguinte da sequence para atribuir ao novo registro.
	 * 
	 * @param regSanguineos {@link AipRegSanguineos}
	 * @return {@link Byte} proximo valor da sequencia.
	 */
	private Byte obterProximoValorSequence(AipRegSanguineos regSanguineos) {
		if (regSanguineos.getId() != null && regSanguineos.getId().getSeqp() != null) {
			return ((byte) (regSanguineos.getId().getSeqp().intValue() + 1));
		} else {
			return ((byte) 1);
		}
	}

	/**
	 * Grava registros alterados/incluidos na tabela MTX_CONTATO_PACIENTES
	 * 
	 * @param listaContatosIncluidos {@link List} de {@link MtxContatoPacientes}
	 * @param paciente {@link AipPacientes}
	 */
	private void gravarContatosPaciente(List<MtxContatoPacientes> listaContatosIncluidos, AipPacientes paciente) {
		
		if (listaContatosIncluidos != null && !listaContatosIncluidos.isEmpty()) {
			for (MtxContatoPacientes contato : listaContatosIncluidos) {
				if (contato.getSeq() != null) {
					this.mtxContatoPacientesDAO.atualizar(contato);
				} else {
					contato.setPaciente(paciente);
					this.mtxContatoPacientesDAO.persistir(contato);
				}
			}
		}
	}
	
	/**
	 * Se registro foi excluido remove da tabela MTX_CONTATO_PACIENTES
	 * 
	 * @param listaContatosExcluidos {@link List} de {@link MtxContatoPacientes}
	 */
	private void removerContatosPaciente(List<MtxContatoPacientes> listaContatosExcluidos) {
		
		if (listaContatosExcluidos != null && !listaContatosExcluidos.isEmpty()) {
			for (MtxContatoPacientes contato : listaContatosExcluidos) {
				this.mtxContatoPacientesDAO.removerPorId(contato.getSeq());
			}
		}
	}
	
	/**
	 * Persiste ou atualiza o registro {@link MtxTransplantes} no banco.
	 * 
	 * @param transplante {@link MtxTransplantes}
	 * @param paciente {@link AipPacientes}
	 * @throws ApplicationBusinessException
	 */
	private void gravar(MtxTransplantes transplante, AipPacientes paciente) throws ApplicationBusinessException {
		
		if (transplante.getSeq() != null) {
			this.mtxTransplantesDAO.atualizar(transplante);
		} else {
			transplante.setReceptor(paciente);
			this.mtxTransplantesDAO.persistir(transplante);
		}
	}
	
	//41770
	/**
	 * Calcula a diferença em anos entre a data de ingresso do paciente com a data atual.
	 */
	public String calcularIdadeIngresso(Date dtIngresso, Date dtNascimento) {
		
		String tempo = ANOS;
		String tempoMes = MESES;
		Integer idadeMes = null;
		String idadeFormat = null;
		if (dtIngresso != null) {
			Calendar dataNascimento = new GregorianCalendar();
			dataNascimento.setTime(dtNascimento);
			Calendar dataCalendario = new GregorianCalendar();
			dataCalendario.setTime(dtIngresso);
			// ObtÃ©m a idade baseado no ano
			Integer idadeNum = dataCalendario.get(Calendar.YEAR) - dataNascimento.get(Calendar.YEAR);

			if (dataCalendario.get(Calendar.MONTH) < dataNascimento.get(Calendar.MONTH)) {
				idadeNum--;
			} else if (dataCalendario.get(Calendar.MONTH) == dataNascimento.get(Calendar.MONTH)
					&& dataCalendario.get(Calendar.DAY_OF_MONTH) < dataNascimento.get(Calendar.DAY_OF_MONTH)) {
				idadeNum--;
			}
			if (idadeNum == UM) {
				tempo = ANO;
			}
			
			if (dataCalendario.get(Calendar.MONTH) < dataNascimento.get(Calendar.MONTH)) {
				idadeMes = dataCalendario.get(Calendar.MONTH) + (12 - dataNascimento.get(Calendar.MONTH));
			} else {
				idadeMes = dataCalendario.get(Calendar.MONTH) - dataNascimento.get(Calendar.MONTH);
				if (dataCalendario.get(Calendar.DAY_OF_MONTH) < dataNascimento.get(Calendar.DAY_OF_MONTH)) {
					idadeMes--;
				}
			}
			
			if(idadeMes<0){
				if (12+(idadeMes) == UM) {
					tempoMes = DOZE+(idadeMes) + MES;
				} else {
					tempoMes = DOZE+(idadeMes) + ESPACO+MESES;
				}	
			}else{
				if (idadeMes == UM) {
					tempoMes = idadeMes + MES;
					
				} else {
					tempoMes = idadeMes + ESPACO + MESES;
				}
			}
			idadeFormat = idadeNum + ESPACO + tempo + ESPACO + tempoMes;
		}

		return idadeFormat;
	}
	
	
	/**
	 * Consulta para obter escore
	 */
	public Double obterEscore(Integer statusDoenca, Date dtIngresso, Date dtNascimento) {
		
		Integer x = null;
		Integer y = 0;
		Integer idade = 0;
		MtxCriterioPriorizacaoTmo coeficiente = null;
		
		if (statusDoenca != null) {
			coeficiente = this.mtxCriterioPriorizacaoTmoDAO.obterCoeficiente(statusDoenca);
			if (coeficiente != null && coeficiente.getGravidade() != null && coeficiente.getCriticidade() != null){
				y = (coeficiente.getGravidade() != null ? coeficiente.getGravidade(): 0 ) + (coeficiente.getCriticidade() != null ? coeficiente.getCriticidade(): 0 );
			}
		}
		if (dtIngresso != null) {
			x = DateUtil.obterQtdDiasEntreDuasDatas(dtIngresso, new Date());
		}
		if (dtNascimento != null) {
			idade = DateUtil.obterQtdAnosEntreDuasDatas(dtNascimento, new Date());
			if (idade < 13) { 
				return (x * 0.33) + y + 20; 
			}
		}
		return (x * 0.33) + y; 
		
	}
	
	/**Persitir Paciente na Lista deTransplante Medula Óssea
	 * @param transplante
	 * @throws ApplicationBusinessException 
	 */
	 public void persitirPacienteListaTransplanteTMO(MtxTransplantes transplante, AipRegSanguineos regSanguineos, AipPacientes paciente, MtxCriterioPriorizacaoTmo statusDoenca, boolean grupoSanguineoFatorAlterado) throws ApplicationBusinessException, BaseListException{
		 RapServidores servidor = new RapServidores(); 
		 servidor = this.registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado());
		 MtxExtratoTransplantes extratoTransplantes = new MtxExtratoTransplantes();
		 
		 this.validarDatasTransplante(transplante, paciente);
		 
		 if(transplante != null){
			 AipPacientes doador = null;
			 if(transplante.getCodDoador() != null){
				 doador  = pacientesDAO.pesquisarPacientePorCodigo(Integer.parseInt(transplante.getCodDoador()));
			 }
			 if(doador != null){
				 transplante.setDoador(doador);
			 }
			 
			//RN02
			transplante.setServidor(servidor);
			if(statusDoenca != null && statusDoenca.getSeq() != null){
				transplante.setCriterioPriorizacao(mtxCriterioPriorizacaoTmoDAO.obterOriginal(statusDoenca.getSeq()));
			}	
			//RN03
			this.gravarRegistroSanguineo(regSanguineos, paciente, this.registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado()));
			 
			 if(transplante.getSeq() == null){
				 //RN01
				 transplante.setCriadoEm(new Date());
				 if (transplante.getTipoTmo().equals(DominioSituacaoTmo.G) && transplante.getTipoAlogenico().equals(DominioTipoAlogenico.N)&& transplante.getDoador() == null){ 
					transplante.setSituacao(DominioSituacaoTransplante.A);
				 } else {
					transplante.setSituacao(DominioSituacaoTransplante.E);
				 }
				   
			
				 this.mtxTransplantesDAO.persistir(transplante);
				 this.mtxTransplantesDAO.flush();
				 
				 //RN05
				 extratoTransplantes.setCriadoEm(new Date());
				 extratoTransplantes.setServidor(servidor);
				 extratoTransplantes.setSituacaoTransplante(transplante.getSituacao());
				 extratoTransplantes.setMtxTransplante(transplante);
				 extratoTransplantes.setDataOcorrencia(new Date());
				 
				 this.mtxExtratoTransplantesDAO.persistir(extratoTransplantes);
				 
			 } else {
				 //RN04
				 gravarJournalTransplante(transplante, DominioOperacoesJournal.UPD);
				 
				 this.mtxTransplantesDAO.merge(transplante);
			 }
		 }
	 }
	 
	 	 
	 /**
	  * Verifica se existe paciente em óbito na lista de pacientes aguardando transplantes realizando atualizações no cadastro.
	  * #41773 ON02
	  * @param filtro
	  * @throws ApplicationBusinessException
	  */
	 private void verificaObitoListaTransplante(ListarTransplantesVO filtro) throws ApplicationBusinessException{

		 AghParametros param = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_COD_MAS_OBITO);
		 Integer masSeq = param != null ? param.getVlrNumerico().intValue() : null;
		 List<MtxTransplantes> listaTransplante = mtxTransplantesDAO.obterTransplantesParaAtualizar(filtro);
		 RapServidores servidor = this.registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado());
		 for(MtxTransplantes transplante : listaTransplante){
			 //RN02
			 gravarJournalTransplante(transplante, DominioOperacoesJournal.UPD);
			 
			 transplante.setServidor(servidor);
			 transplante.setSituacao(DominioSituacaoTransplante.I);
			 //RN01
			 mtxTransplantesDAO.atualizar(transplante);
			 
			 MtxMotivoAlteraSituacao motivoAlteracao =  mtxMotivoAlteraSituacaoDAO.obterPorChavePrimaria(masSeq);
			 MtxExtratoTransplantes extratoTransplantes = new MtxExtratoTransplantes();
			 extratoTransplantes.setMtxTransplante(transplante);
			 extratoTransplantes.setDataOcorrencia(Calendar.getInstance().getTime());
			 extratoTransplantes.setMotivoAlteraSituacao(motivoAlteracao);
			 extratoTransplantes.setSituacaoTransplante(DominioSituacaoTransplante.I);
			 extratoTransplantes.setCriadoEm(Calendar.getInstance().getTime());
			 extratoTransplantes.setServidor(servidor);
			 //RNO3
			 mtxExtratoTransplantesDAO.persistir(extratoTransplantes);
		 }
	 }

	private void gravarJournalTransplante(MtxTransplantes transplante, DominioOperacoesJournal operacao) throws ApplicationBusinessException{
		RapServidores servidor = this.registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado());
		MtxTransplantesJn journal = new MtxTransplantesJn();
		 if(transplante.getCid() != null){
			 journal.setCidSeq(transplante.getCid().getSeq());
		 }
		 
		 journal.setSeq(transplante.getSeq());
		 journal.setCodDoador(transplante.getCodDoador());
		 journal.setCriadoEm(transplante.getCriadoEm());
		 journal.setDataDiagnostico(transplante.getDataDiagnostico());
		 journal.setDataDialise(transplante.getDataDialise());
		 journal.setDataFistula(transplante.getDataFistula());
		 journal.setDataIngresso(transplante.getDataIngresso());
		 journal.setDataUltimaGestacao(transplante.getDataUltimaGestacao());
		 journal.setDataUltimaTransfusao(transplante.getDataUltimaTransfusao());
		 journal.setDataUltimoTransplante(transplante.getDataUltimoTransplante());
		 if(transplante.getDoencaBase() != null){
			 journal.setDobSeq(transplante.getDoencaBase().getSeq());
		 }
		 journal.setNroGestacoes(transplante.getNroGestacoes());
		 journal.setNroTransfusoes(transplante.getNroTransfusoes());
		 journal.setNroTransplantes(transplante.getNroTransplantes());
		 journal.setObservacoes(transplante.getObservacoes());
		 if(transplante.getOrigem() != null){
			 journal.setOrgSeq(transplante.getOrigem().getSeq());
		 }
		 if(transplante.getDoador() != null){
			 journal.setPacCodigoDoador(transplante.getDoador().getCodigo());
		 }
		 if(transplante.getReceptor() != null){
			 journal.setPacCodigoReceptor(transplante.getReceptor().getCodigo());
		 }
		 journal.setRgct(transplante.getRgct());
		 if(transplante.getServidor() != null){
			 journal.setSerMatricula(transplante.getServidor().getId().getMatricula());
			 journal.setSerVinCodigo(transplante.getServidor().getId().getVinCodigo());
		 }
		 if(transplante.getSituacao() != null){
			 journal.setSituacao(transplante.getSituacao().toString());
		 }
		 if(transplante.getTipoOrgao() != null){
			 journal.setTipoOrgao(transplante.getTipoOrgao().toString());
		 }
		 if(transplante.getTipoTmo() != null){
			 journal.setTipoTmo(transplante.getTipoTmo().toString());
		 }
		 
		 journal.setNomeUsuario(servidor.getUsuario());
		 journal.setOperacao(operacao);
		 journal.setVersion(transplante.getVersion());
		 
		 mtxTransplantesJnDAO.persistir(journal);
		 mtxTransplantesJnDAO.flush();
	}
	 
	 public List<ListarTransplantesVO> obterPacientesAguardandoTransplantePorFiltro(ListarTransplantesVO filtro, Integer firstResult, Integer maxResults, 
				String orderProperty, boolean asc)throws ApplicationBusinessException{
		 this.verificaObitoListaTransplante(filtro);
		 return mtxTransplantesDAO.obterPacientesAguardandoTransplantePorFiltro(filtro, firstResult, maxResults, orderProperty, asc);
	 }
	 
	 /**
	  * 
	  * @param filtro
	  * @param firstResult
	  * @param maxResults
	  * @param orderProperty
	  * @param asc
	  * @return
	  * @throws ApplicationBusinessException
	  */
	 public List<ListarTransplantesVO> obterPacientesTransplantadosPorFiltro(ListarTransplantesVO filtro, Integer firstResult, Integer maxResults, 
				String orderProperty, boolean asc)throws ApplicationBusinessException{
		 List<ListarTransplantesVO> lista = new ArrayList<ListarTransplantesVO>();
		 lista.addAll(mtxTransplantesDAO.obterPacientesAguardandoTransplantePorFiltro(filtro, firstResult, maxResults, orderProperty, asc));
		 return lista;
	 }

	public String obterIdadeFormatada(Date dataNascimento){
		Integer dias = DateUtil.getIdadeDias(dataNascimento);
		if(dias > 364){
			return StringUtils.EMPTY+DateUtil.getIdade(dataNascimento) + " anos";
		}
		else if(dias < 365 && dias > 30){
			return StringUtils.EMPTY+DateUtil.getIdadeMeses(dataNascimento) + " meses";
		}
		else{
			return StringUtils.EMPTY+DateUtil.getIdadeDias(dataNascimento) + " dias";
		}
	}
	
	
	/**
	 * #41798 ON1
	 * @throws CloneNotSupportedException 
	 */
	public void verificarDoencasPacientesON(List<PacienteAguardandoTransplanteOrgaoVO> listaRetorno, boolean isCount) throws CloneNotSupportedException, ApplicationBusinessException{
		
		for (Iterator<PacienteAguardandoTransplanteOrgaoVO> iterator = listaRetorno.iterator(); iterator.hasNext();) {
			PacienteAguardandoTransplanteOrgaoVO registroPaciente = iterator.next();
			if (!isCount) {
				registroPaciente.setTemGrm(this.mciNotificacaoGmrDAO.verificarNotificacaoGmrPorCodigo(registroPaciente.getCodigoReceptor()));
				registroPaciente.setTemDiabetes(this.mamDiagnosticoDAO.verificarPacienteTemDiabetesPorPacCodigo(registroPaciente.getCodigoReceptor()));
				registroPaciente.setTemHIV(this.mamDiagnosticoDAO.verificarPacienteHIVPositivoPorPacCodigo(registroPaciente.getCodigoReceptor()));
				registroPaciente.setTemHepatiteB(this.mamDiagnosticoDAO.verificarPacienteTemHepatiteBPorPacCodigo(registroPaciente.getCodigoReceptor()));
				registroPaciente.setTemHepatiteC(this.mamDiagnosticoDAO.verificarPacienteTemHepatiteCPorPacCodigo(registroPaciente.getCodigoReceptor()));
				registroPaciente.setExisteResultadoExame(this.aelResultadoExameDAO.verificarExisteResultadoExame(registroPaciente.getCodigoReceptor()));

			}
			if (validaDataObitoPacienteON(registroPaciente)) {
				iterator.remove();
			}
		}
	}
	
	/**
	 * #41798 ON1
	 * @throws CloneNotSupportedException 
	 */
	public void verificarDoencasPacientesRetiradosON(List<PacienteAguardandoTransplanteOrgaoVO> listaRetorno) throws CloneNotSupportedException, ApplicationBusinessException{
		
		for (PacienteAguardandoTransplanteOrgaoVO registroPaciente : listaRetorno) {
				registroPaciente.setTemGrm(this.mciNotificacaoGmrDAO.verificarNotificacaoGmrPorCodigo(registroPaciente.getCodigoReceptor()));
				registroPaciente.setTemDiabetes(this.mamDiagnosticoDAO.verificarPacienteTemDiabetesPorPacCodigo(registroPaciente.getCodigoReceptor()));
				registroPaciente.setTemHIV(this.mamDiagnosticoDAO.verificarPacienteHIVPositivoPorPacCodigo(registroPaciente.getCodigoReceptor()));
				registroPaciente.setTemHepatiteB(this.mamDiagnosticoDAO.verificarPacienteTemHepatiteBPorPacCodigo(registroPaciente.getCodigoReceptor()));
				registroPaciente.setTemHepatiteC(this.mamDiagnosticoDAO.verificarPacienteTemHepatiteCPorPacCodigo(registroPaciente.getCodigoReceptor()));
				registroPaciente.setExisteResultadoExame(this.aelResultadoExameDAO.verificarExisteResultadoExame(registroPaciente.getCodigoReceptor()));
		}
	}
	
	/**
	 * ON2
	 * @param registroPaciente
	 * @throws CloneNotSupportedException
	 */
	public boolean validaDataObitoPacienteON(PacienteAguardandoTransplanteOrgaoVO registroPaciente) throws CloneNotSupportedException, ApplicationBusinessException{
		
		if(registroPaciente.getPacDataObito() != null){
			String login = obterLoginUsuarioLogado();
			RapServidores servidorLogado = this.registroColaboradorFacade.obterServidorPorUsuario(obterLoginUsuarioLogado());

			MtxTransplantes mtxTransplante = this.mtxTransplantesDAO.obterPorChavePrimaria(registroPaciente.getSeqTransplante());
			MtxTransplantes mtxTransplanteOriginal = (MtxTransplantes) mtxTransplante.clone();
			persistirMtxTransplantesJournay(mtxTransplanteOriginal, login);
			
			// #41798 - RN01
			mtxTransplante.setSituacao(DominioSituacaoTransplante.R);
			mtxTransplante.setServidor(servidorLogado);
			this.mtxTransplantesDAO.atualizar(mtxTransplante);
			
			gravarExtratoTransplantesRN(mtxTransplante, servidorLogado);
			
			return true;
		}
		else{
			return false;
		}
		
	}
	
	/**
	 * #41798 - RN02
	 * @param original
	 * @param login
	 */
	private void persistirMtxTransplantesJournay(MtxTransplantes original, String login){
		
		MtxTransplantesJn mtxTransplantesJn = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.UPD, MtxTransplantesJn.class, 
				!login.trim().isEmpty() ? login : null);
		
		mtxTransplantesJn.setCidSeq(original.getCidSeq());
		mtxTransplantesJn.setCodDoador(original.getCodDoador());
		mtxTransplantesJn.setCriadoEm(original.getCriadoEm());
		mtxTransplantesJn.setDataDiagnostico(original.getDataDiagnostico());
		mtxTransplantesJn.setDataDialise(original.getDataDialise());
		mtxTransplantesJn.setDataFistula(original.getDataFistula());
		mtxTransplantesJn.setDataIngresso(original.getDataIngresso());
		mtxTransplantesJn.setDataUltimaGestacao(original.getDataUltimaGestacao());
		mtxTransplantesJn.setDataUltimaTransfusao(original.getDataUltimaTransfusao());
		mtxTransplantesJn.setDataUltimoTransplante(original.getDataUltimoTransplante());
		mtxTransplantesJn.setDobSeq(original.getDoencasBaseSeq());
		mtxTransplantesJn.setNroGestacoes(original.getNroGestacoes());
		mtxTransplantesJn.setNroTransfusoes(original.getNroTransfusoes());
		mtxTransplantesJn.setNroTransplantes(original.getNroTransplantes());
		mtxTransplantesJn.setObservacoes(original.getObservacoes());
		mtxTransplantesJn.setOrgSeq(original.getOrigemSeq());
		mtxTransplantesJn.setPacCodigoDoador(original.getPacCodDoador());
		mtxTransplantesJn.setPacCodigoReceptor(original.getPacCodReceptor());
		mtxTransplantesJn.setRgct(original.getRgct());
		mtxTransplantesJn.setSeq(original.getSeq());
		
		if(original.getServidor() != null){
			if(original.getServidor().getId() != null){
				mtxTransplantesJn.setSerVinCodigo(original.getServidor().getId().getVinCodigo());
				mtxTransplantesJn.setSerMatricula(original.getServidor().getId().getMatricula());
			}
		}
		
		if(original.getSituacao() != null){
			mtxTransplantesJn.setSituacao(original.getSituacao().toString());
		}
		
		if(original.getTipoOrgao() != null){
			mtxTransplantesJn.setTipoOrgao(original.getTipoOrgao().toString());
		}
		if(original.getTipoTmo() != null){
			mtxTransplantesJn.setTipoTmo(original.getTipoTmo().toString());
		}
		
		this.mtxTransplantesJnDAO.persistir(mtxTransplantesJn);
	}
	
	/**
	 * #41798 - RN03
	 * @param mtxTransplantes
	 * @param servidorLogado
	 */
	private void gravarExtratoTransplantesRN(MtxTransplantes mtxTransplantes, RapServidores servidorLogado) throws ApplicationBusinessException{
		AghParametros param =  parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_COD_MAS_OBITO);
		Integer masSeq = param != null ? param.getVlrNumerico().intValue() : null;
		MtxExtratoTransplantes mtxExtratoTransplantes = new MtxExtratoTransplantes();
		mtxExtratoTransplantes.setMtxTransplante(mtxTransplantes);
		mtxExtratoTransplantes.setDataOcorrencia(new Date());
		
		MtxMotivoAlteraSituacao mtxMotivoAlteraSituacao = mtxMotivoAlteraSituacaoDAO.obterPorChavePrimaria(masSeq);
		mtxExtratoTransplantes.setMotivoAlteraSituacao(mtxMotivoAlteraSituacao);
		mtxExtratoTransplantes.setSituacaoTransplante(DominioSituacaoTransplante.R);
		mtxExtratoTransplantes.setCriadoEm(new Date());
		mtxExtratoTransplantes.setServidor(servidorLogado);
		
		this.mtxExtratoTransplantesDAO.persistir(mtxExtratoTransplantes);
		
	}
	
	/**
	 * @author thiago.cortes
	 * #41801
	 */
	
	public List<PacienteAguardandoTransplanteOrgaoVO> obterListaPacientesRetiradosOrgao(FiltroTransplanteOrgaoVO filtro, Integer firstResult, Integer maxResults, 
			String orderProperty, boolean asc, boolean paginacao) throws CloneNotSupportedException, ApplicationBusinessException{
		List<PacienteAguardandoTransplanteOrgaoVO> lista = new ArrayList<PacienteAguardandoTransplanteOrgaoVO>();
		lista = mtxTransplantesDAO.obterListaPacientesAguardandoTransplanteOrgao(filtro, firstResult, maxResults, orderProperty, asc, paginacao);
		verificarDoencasPacientesRetiradosON(lista);
		return lista;
	}
	
	/**
	 * #41798 ON1
	 * @throws CloneNotSupportedException 
	 */
	public void verificarDoencasPacientesInativoON(List<PacienteAguardandoTransplanteOrgaoVO> listaRetorno) throws CloneNotSupportedException, ApplicationBusinessException{
		
		for (PacienteAguardandoTransplanteOrgaoVO registroPaciente : listaRetorno) {
			registroPaciente.setTemGrm(this.mciNotificacaoGmrDAO.verificarNotificacaoGmrPorCodigo(registroPaciente.getCodigoReceptor()));
			registroPaciente.setTemDiabetes(this.mamDiagnosticoDAO.verificarPacienteTemDiabetesPorPacCodigo(registroPaciente.getCodigoReceptor()));
			registroPaciente.setTemHIV(this.mamDiagnosticoDAO.verificarPacienteHIVPositivoPorPacCodigo(registroPaciente.getCodigoReceptor()));
			registroPaciente.setTemHepatiteB(this.mamDiagnosticoDAO.verificarPacienteTemHepatiteBPorPacCodigo(registroPaciente.getCodigoReceptor()));
			registroPaciente.setTemHepatiteC(this.mamDiagnosticoDAO.verificarPacienteTemHepatiteCPorPacCodigo(registroPaciente.getCodigoReceptor()));
			registroPaciente.setExisteResultadoExame(this.aelResultadoExameDAO.verificarExisteResultadoExame(registroPaciente.getCodigoReceptor()));
			registroPaciente.setDataRegistroInativado(this.mtxExtratoTransplantesDAO.obterDataTransplanteInativado(registroPaciente.getSeqTransplante()));
		}
	}
	
	/**
	 * @author thiago.cortes
	 * #41807
	 */
	public List<PacienteTransplantadosOrgaoVO> obterListaPacientesTransplantadosOrgao(FiltroTransplanteOrgaoVO filtro, Integer firstResult, Integer maxResults, 
			String orderProperty, boolean asc){
		List<PacienteTransplantadosOrgaoVO> lista = new ArrayList<PacienteTransplantadosOrgaoVO>();
		lista = mtxTransplantesDAO.obterListaPacientesTransplantadosOrgao(filtro, firstResult, maxResults, orderProperty, asc);
		verificarDoencasPacientesTransplantadosON(lista);
		return lista;
	}
	
	/**
	 * #41807 ON1
	 * @throws CloneNotSupportedException 
	 */
	public void verificarDoencasPacientesTransplantadosON(List<PacienteTransplantadosOrgaoVO> listaRetorno){
		
		for (PacienteTransplantadosOrgaoVO registroPaciente : listaRetorno) {
			
			registroPaciente.setTemGrm(this.mciNotificacaoGmrDAO.verificarNotificacaoGmrPorCodigo(registroPaciente.getCodigoReceptor()));
			registroPaciente.setTemDiabetes(this.mamDiagnosticoDAO.verificarPacienteTemDiabetesPorPacCodigo(registroPaciente.getCodigoReceptor()));
			registroPaciente.setTemHIV(this.mamDiagnosticoDAO.verificarPacienteHIVPositivoPorPacCodigo(registroPaciente.getCodigoReceptor()));
			registroPaciente.setTemHepatiteB(this.mamDiagnosticoDAO.verificarPacienteTemHepatiteBPorPacCodigo(registroPaciente.getCodigoReceptor()));
			registroPaciente.setTemHepatiteC(this.mamDiagnosticoDAO.verificarPacienteTemHepatiteCPorPacCodigo(registroPaciente.getCodigoReceptor()));
			registroPaciente.setExisteResultadoExame(this.aelResultadoExameDAO.verificarExisteResultadoExame(registroPaciente.getCodigoReceptor()));
			
		}
	}
	
	/**
	 * #41787 Muda Status do paciente e grava Extrato Transplante com Motivo aplicando RN1, RN2 e RN3
	 * @param trpSeq
	 * @param extratoTransplante
	 */
	public boolean mudarStatusPacienteTMO(Integer trpSeq, DominioSituacaoTransplante situacao, Integer pacCodigo, Integer masSeq) throws ApplicationBusinessException{
		
		boolean retorno = true;
		RapServidores servidor = this.registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado());
		if (trpSeq != null) {
			MtxTransplantes transplantes = mtxTransplantesDAO.obterPorChavePrimaria(trpSeq);
			
			if (transplantes != null && transplantes.getSeq() != null) {
				//RN2	
				this.gravarJournalTransplante(transplantes, DominioOperacoesJournal.UPD);
				//RN1	
				transplantes.setServidor(servidor);
				transplantes.setSituacao(situacao);
				
				if(situacao.equals(DominioSituacaoTransplante.T)){
					//RN03
					retorno = this.gravarFatItemContaHospitalar(pacCodigo, transplantes, servidor);
				}
					
				this.mtxTransplantesDAO.atualizar(transplantes);
				this.mtxTransplantesDAO.flush();
				MtxExtratoTransplantes extratoTransplante = new MtxExtratoTransplantes();
				if (masSeq != null) {
					MtxMotivoAlteraSituacao motivo = mtxMotivoAlteraSituacaoDAO.obterPorChavePrimaria(masSeq);
					extratoTransplante.setMotivoAlteraSituacao(motivo);
				}
				extratoTransplante.setMtxTransplante(transplantes);
				extratoTransplante.setDataOcorrencia(new Date());
				extratoTransplante.setSituacaoTransplante(situacao);
				extratoTransplante.setCriadoEm(new Date());
				extratoTransplante.setServidor(servidor);
				this.mtxExtratoTransplantesDAO.persistir(extratoTransplante);
			}
		}
		return retorno;
	}
	
	private boolean gravarFatItemContaHospitalar(Integer pacCodigo, MtxTransplantes transplantes, RapServidores servidor){
		
		List<FatContasHospitalares> listaContasPaciente = this.aghAtendimentoDAO.obterContaPaciente(pacCodigo);
		if (listaContasPaciente != null && !listaContasPaciente.isEmpty()) {
			for (FatContasHospitalares element : listaContasPaciente) {
				
				FatItemContaHospitalar fatItemContaHospitalar = new FatItemContaHospitalar();
					fatItemContaHospitalar.setId(new FatItemContaHospitalarId());
				fatItemContaHospitalar.getId().setSeq(fatItemContaHospitalarDAO
						.buscaMaxSeqMaisUm(element.getSeq()));
				fatItemContaHospitalar.getId().setCthSeq(element.getSeq());
				fatItemContaHospitalar.setIchType(DominioItemConsultoriaSumarios.ICH);
				
				FatProcedHospInternos fatProcedHospInternos = retornarProcedimentoHospitalar(transplantes);
				if (fatProcedHospInternos != null) {
					fatItemContaHospitalar.setProcedimentoHospitalarInterno(fatProcedHospInternos);
				}
				Date dataAtual = new Date();
				fatItemContaHospitalar.setCriadoEm(dataAtual);
				fatItemContaHospitalar.setCriadoPor(servidor.getUsuario());
				fatItemContaHospitalar.setIndSituacao(DominioSituacaoItenConta.A);
				Short quantRealizada = 1;
				fatItemContaHospitalar.setQuantidadeRealizada(quantRealizada);
				fatItemContaHospitalar.setIndOrigem(DominioIndOrigemItemContaHospitalar.BCC);
				fatItemContaHospitalar.setLocalCobranca(DominioLocalCobranca.I);
				fatItemContaHospitalar.setDthrRealizado(dataAtual);
				fatItemContaHospitalarDAO.persistir(fatItemContaHospitalar);
				fatItemContaHospitalarDAO.flush();
			}
			return true;
		}else{
			return false;
		}
	}

	private FatProcedHospInternos retornarProcedimentoHospitalar(MtxTransplantes transplantes) {
		Integer phiSeq = null;
		try {
			if (transplantes.getTipoTmo() != null) {
				if (transplantes.getTipoTmo().equals(DominioSituacaoTmo.U)) {
					phiSeq = this.parametroFacade.buscarValorInteiro(AghuParametrosEnum.P_AGHU_PHI_TMO_AUTOLOGO);
					return fatProcedHospInternosDAO.obterPorChavePrimaria(phiSeq);
				}
				else if (transplantes.getTipoTmo().equals(DominioSituacaoTmo.G) && transplantes.getTipoAlogenico() != null) {
					
					if (transplantes.getTipoAlogenico().equals(DominioTipoAlogenico.A)) {
						phiSeq = this.parametroFacade.buscarValorInteiro(AghuParametrosEnum.P_AGHU_PHI_TMO_ALOG_APAR);
						return fatProcedHospInternosDAO.obterPorChavePrimaria(phiSeq);
					}
					else if(transplantes.getTipoAlogenico().equals(DominioTipoAlogenico.N)){
					 phiSeq = this.parametroFacade.buscarValorInteiro(AghuParametrosEnum.P_AGHU_PHI_TMO_ALOG_NAO_APAR);
					 return fatProcedHospInternosDAO.obterPorChavePrimaria(phiSeq);
					}
				}
			}
		} catch (Exception e) {
			LOG.error("Exceção capturada: ", e);
		}
		return null;
		
	}
	
	/**
	 * #41799 Muda Status do paciente e grava Extrato Transplante com Motivo aplicando RN1, RN2
	 * @param trpSeq
	 * @param extratoTransplante
	 */
	public boolean mudarStatusPacienteRins(Integer trpSeq, DominioSituacaoTransplante situacao, Integer masSeq) throws ApplicationBusinessException{
		boolean retorno = true;
		RapServidores servidor = this.registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado());
		if (trpSeq != null) {
			MtxTransplantes transplantes = mtxTransplantesDAO.obterPorChavePrimaria(trpSeq);
			
			if (transplantes != null && transplantes.getSeq() != null) {
				//RN2	
				this.gravarJournalTransplante(transplantes, DominioOperacoesJournal.UPD);
				//RN1	
				transplantes.setServidor(servidor);
				transplantes.setSituacao(situacao);
				this.mtxTransplantesDAO.atualizar(transplantes);
				this.mtxTransplantesDAO.flush();
				
				MtxExtratoTransplantes extratoTransplante = new MtxExtratoTransplantes();
				if (masSeq != null) {
					MtxMotivoAlteraSituacao motivo = mtxMotivoAlteraSituacaoDAO.obterPorChavePrimaria(masSeq);
					extratoTransplante.setMotivoAlteraSituacao(motivo);
				}
				extratoTransplante.setMtxTransplante(transplantes);
				extratoTransplante.setDataOcorrencia(new Date());
				extratoTransplante.setSituacaoTransplante(situacao);
				extratoTransplante.setCriadoEm(new Date());
				extratoTransplante.setServidor(servidor);
				this.mtxExtratoTransplantesDAO.persistir(extratoTransplante);
			}
		}
		return retorno;
	}
	public void registarTransplantes(List<MbcProcedimentoCirurgicos> listaProcedimentos, Integer codPaciente) throws ApplicationBusinessException {

		if (habilitadoRegistroTransplantes()){

			List<MtxProcedimentoTransplantes> procedimentos = mtxProcedimentoTransplantesDAO.procedimentosVinculadosAoOrgao(listaProcedimentos); // C1

			for (MtxProcedimentoTransplantes item : procedimentos) {

				MtxTransplantes mtxTransplantes = mtxTransplantesDAO.obterTransplantePorPacienteOrgao(codPaciente, item.getOrgao()); // C2

				if (mtxTransplantes != null) {

					mtxTransplantes = mtxTransplantesDAO.obterPorChavePrimaria(mtxTransplantes.getSeq());

					mtxTransplantes.setSituacao(DominioSituacaoTransplante.T);
					mtxTransplantesDAO.merge(mtxTransplantes);

					MtxExtratoTransplantes mtxExtratoTransplantes = new MtxExtratoTransplantes();
					mtxExtratoTransplantes.setMotivoAlteraSituacao(obterMotivoAlteraSituacao());
					mtxExtratoTransplantes.setMtxTransplante(mtxTransplantes);
					mtxExtratoTransplantes.setDataOcorrencia(new Date());
					mtxExtratoTransplantes.setSituacaoTransplante(DominioSituacaoTransplante.T);
					mtxExtratoTransplantes.setCriadoEm(new Date());
					mtxExtratoTransplantes.setServidor(registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado()));
					mtxExtratoTransplantesDAO.persistir(mtxExtratoTransplantes);

					mtxTransplantesDAO.flush();
				}
			}
		}
	}

	private Boolean habilitadoRegistroTransplantes() throws ApplicationBusinessException {
		AghParametros parametro = parametroFacade.obterAghParametro(AghuParametrosEnum.P_AGHU_HABILITA_REGISTRO_TRANSPLANTE);

		if (parametro.getVlrTexto().equalsIgnoreCase("S")) {
			return true;
		}
		return false;
	}

	private MtxMotivoAlteraSituacao obterMotivoAlteraSituacao() throws ApplicationBusinessException {
		MtxMotivoAlteraSituacao mtxMotivoAlteraSituacao = new MtxMotivoAlteraSituacao();

		AghParametros parametro = parametroFacade.obterAghParametro(AghuParametrosEnum.P_AGHU_MAS_SEQ_TRP_ORGAOS);

		mtxMotivoAlteraSituacao.setSeq(parametro.getVlrNumerico().intValue());

		return mtxMotivoAlteraSituacaoDAO.obterPorChavePrimaria(mtxMotivoAlteraSituacao.getSeq());
	}
	 
	public List<MtxTransplantes> obtemTransplantados(DominioSituacaoTmo tipoTmo, Integer codPacienteReceptor) {
		return mtxTransplantesDAO.verificaTransplanteJaExistente(tipoTmo, codPacienteReceptor);
				
	}
}
