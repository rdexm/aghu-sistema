package br.gov.mec.aghu.ambulatorio.business;

import java.text.DateFormat;
import java.text.ParseException;
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
import br.gov.mec.aghu.ambulatorio.dao.AacConsultasDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacGradeAgendamenConsultasDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamConsultorAmbulatorioDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamInterconsultasDAO;
import br.gov.mec.aghu.ambulatorio.vo.SolicitaInterconsultaVO;
import br.gov.mec.aghu.configuracao.dao.AghCaixaPostalAplicacaoDAO;
import br.gov.mec.aghu.configuracao.dao.AghCaixaPostalDAO;
import br.gov.mec.aghu.configuracao.dao.AghCaixaPostalServidorDAO;
import br.gov.mec.aghu.configuracao.dao.AghEquipesDAO;
import br.gov.mec.aghu.configuracao.dao.AghEspecialidadesDAO;
import br.gov.mec.aghu.configuracao.dao.AghParametroAplicacaoDAO;
import br.gov.mec.aghu.configuracao.dao.MamInterconsultaJnDAO;
import br.gov.mec.aghu.dominio.DominioAvaliacaoInterconsulta;
import br.gov.mec.aghu.dominio.DominioFormaIdentificacaoCaixaPostal;
import br.gov.mec.aghu.dominio.DominioIndPendenteAmbulatorio;
import br.gov.mec.aghu.dominio.DominioOperacaoBanco;
import br.gov.mec.aghu.dominio.DominioPrioridadeInterconsultas;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacaoCxtPostalServidor;
import br.gov.mec.aghu.dominio.DominioSituacaoInterconsultasPesquisa;
import br.gov.mec.aghu.dominio.DominioTipoMensagemExame;
import br.gov.mec.aghu.dominio.DominioTipoNomeParam;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AghCaixaPostal;
import br.gov.mec.aghu.model.AghCaixaPostalAplicacao;
import br.gov.mec.aghu.model.AghCaixaPostalAplicacaoId;
import br.gov.mec.aghu.model.AghCaixaPostalServidor;
import br.gov.mec.aghu.model.AghCaixaPostalServidorId;
import br.gov.mec.aghu.model.AghEquipes;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghParametroAplicacao;
import br.gov.mec.aghu.model.AghParametroAplicacaoId;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MamConsultorAmbulatorio;
import br.gov.mec.aghu.model.MamInterconsultaJn;
import br.gov.mec.aghu.model.MamInterconsultas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.paciente.dao.AipPacientesDAO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.registrocolaborador.dao.RapServidoresDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class GestaoInterconsultasRN extends BaseBusiness {

	private static final long serialVersionUID = 4295288491074448465L;
	private static final Log LOG = LogFactory.getLog(GestaoInterconsultasRN.class);
	private static final String MAMF_LISTA_CONS_AMB = "MAMF_LISTA_CONS_AMB";
	
	@Override
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@Inject
	private MamInterconsultasDAO mamInterconsultasDAO;

	@Inject
	private AacConsultasDAO aacConsultasDAO;
	
	@Inject
	private AghEspecialidadesDAO aghEspecialidadesDAO;

	@Inject
	private MamConsultorAmbulatorioDAO mamConsultorAmbulatorioDAO;

	@Inject
	private AghCaixaPostalServidorDAO aghCaixaPostalServidorDAO;
	
	@Inject
	private AghCaixaPostalAplicacaoDAO aghCaixaPostalAplicacaoDAO;

	@Inject
	private AghParametroAplicacaoDAO aghParametroAplicacaoDAO;

	@Inject
	private AacGradeAgendamenConsultasDAO aacGradeAgendamenConsultasDAO;

	@Inject
	private MamInterconsultaJnDAO interconsultaJnDAO;

	@Inject
	private AghCaixaPostalDAO aghCaixaPostalDAO;

	@Inject
	private AghEquipesDAO aghEquipesDAO;
	
	@Inject
	private RapServidoresDAO rapServidoresDAO;
	
	@Inject
	private AipPacientesDAO aipPacientesDAO;
	

	
	public enum GestaoInterconsultasRNExceptionCode implements BusinessExceptionCode {
		DATA_INICIAL_MAIOR_QUE_DATA_FINAL_INTERCONSULTAS, DATA_INICIAL_NULA, MAM_05003, MAM_05004, MAM_03536,
		GESTAO_INTERCONSULTA_INT_OU_CONS_RETORNO, MAM_01745, MAM_05091, MAM_05092, MAM_05099, MAM_05094, MAM_00605, MAM_03429, MAM_02915,
		MAM_01638, MAM_01694, MAM_01635, MAM_01666, CONSULTORIA_AVALIADA_SEM_INDICACAO_NOVA_CONSULTA;
	}
	
	/**
	 * ORADB: MAMT_IEO_ARI, MAMT_IEO_ASI, MAMT_IEO_BRI, MAMT_IEO_BSI,
	 * MAMT_IEO_ARU, MAMT_IEO_ASU, MAMT_IEO_BRU, MAMT_IEO_BSU. 
	 * As triggers MAMT_IEO_ARU, MAMT_IEO_ASU, MAMT_IEO_BRU, MAMT_IEO_BSU serão
	 * implementadas na estória #40229. 
	 * As demais triggers deverão ser implementadas nas próximas estórias, 
	 * quando necessário.
	 * @throws ApplicationBusinessException 
	 */
	public void persistirInterconsulta(MamInterconsultas interconsulta) throws ApplicationBusinessException {	
		MamInterconsultas oldMamInterconsultas = mamInterconsultasDAO.obterOriginal(interconsulta);
		preAtualizarInterconsulta(oldMamInterconsultas, interconsulta);
		mamInterconsultasDAO.atualizar(interconsulta);	
		posAtualizarInterconsulta(oldMamInterconsultas, interconsulta);
	}
	
	/**
	 * Atualiza pendente na exclusão.
	 * 
	 * @param excluirInterconsultas
	 * @throws ApplicationBusinessException
	 */
	public void excluirInterconsultas(MamInterconsultas excluirInterconsultas) throws ApplicationBusinessException{
		
		if (excluirInterconsultas.getIndCanceladoObito() != null && excluirInterconsultas.getIndCanceladoObito().equals(DominioSimNao.S.toString())){
			throw new ApplicationBusinessException(GestaoInterconsultasRNExceptionCode.MAM_03536);
		}else if (excluirInterconsultas.getPendente() != null && excluirInterconsultas.getPendente().equals(DominioIndPendenteAmbulatorio.C)){
			excluirInterconsultas.setPendente(DominioIndPendenteAmbulatorio.V);						
		}else {
			excluirInterconsultas.setPendente(DominioIndPendenteAmbulatorio.C);
		}		
		mamInterconsultasDAO.atualizar(excluirInterconsultas);		
	}

	/**
	 * Atualiza a situação do avisado.
	 * 
	 * @param avisarInterconsultas
	 * @param foiAvisado
	 * @throws ApplicationBusinessException
	 */
	public void atualizarInterconsulta(MamInterconsultas interconsultas, String foiAvisado) throws ApplicationBusinessException{
		
		if (foiAvisado.equals(DominioSimNao.N.toString()) && interconsultas.getSituacao().equals(DominioSituacaoInterconsultasPesquisa.M)){
			interconsultas.setSituacao(DominioSituacaoInterconsultasPesquisa.A);
		}else if (foiAvisado.equals(DominioSimNao.N.toString()) && !interconsultas.getSituacao().equals(DominioSituacaoInterconsultasPesquisa.M) && interconsultas.getDthrAvisaRetorno() == null) {
			interconsultas.setDthrAvisaRetorno(new Date());
		}else if (foiAvisado.equals(DominioSimNao.N.toString()) && !interconsultas.getSituacao().equals(DominioSituacaoInterconsultasPesquisa.M) && interconsultas.getDthrAvisaRetorno() != null) {
			throw new ApplicationBusinessException(GestaoInterconsultasRNExceptionCode.MAM_05003);						
		}else if (foiAvisado.equals(DominioSimNao.S.toString()) && interconsultas.getSituacao().equals(DominioSituacaoInterconsultasPesquisa.A)){
			interconsultas.setSituacao(DominioSituacaoInterconsultasPesquisa.M);
		}else if (foiAvisado.equals(DominioSimNao.S.toString()) && !interconsultas.getSituacao().equals(DominioSituacaoInterconsultasPesquisa.A) && interconsultas.getDthrAvisaRetorno() != null) {
			interconsultas.setDthrAvisaRetorno(null);
		}else if (foiAvisado.equals(DominioSimNao.S.toString()) && !interconsultas.getSituacao().equals(DominioSituacaoInterconsultasPesquisa.A) && interconsultas.getDthrAvisaRetorno() == null) {
			throw new ApplicationBusinessException(GestaoInterconsultasRNExceptionCode.MAM_05004);
		}
		
		persistirInterconsulta(interconsultas);
	}
	
	/**
	 * Método que valida datas da pesquisa
	 * @param dataInicial
	 * @param dataFinal
	 * @throws ApplicationBusinessException
	 */
	public void validarDatas(Date dataInicial, Date dataFinal) throws ApplicationBusinessException{
		if (DateUtil.validaDataMaior(dataInicial, dataFinal)){
			throw new ApplicationBusinessException(GestaoInterconsultasRNExceptionCode.DATA_INICIAL_MAIOR_QUE_DATA_FINAL_INTERCONSULTAS);
		}

		if (dataInicial == null && dataFinal != null){
			throw new ApplicationBusinessException(GestaoInterconsultasRNExceptionCode.DATA_INICIAL_NULA);
		}
	}	
	public List<MamInterconsultas> listaInterconsultas(final Integer firstResult, final Integer maxResult, final String orderProperty, final boolean asc,
			final MamInterconsultas mamInterconsultas, Date dataInicial, Date dataFinal, boolean consultoria, boolean excluidos) {
		return this.mamInterconsultasDAO.listaInterconsultas(firstResult, maxResult, orderProperty, asc, mamInterconsultas, dataInicial, dataFinal, consultoria, excluidos);
	}
	public Long listaInterconsultasCount(MamInterconsultas mamInterconsultas, Date dataInicial, Date dataFinal, boolean consultoria, boolean excluidos){		
		return this.mamInterconsultasDAO.listaInterconsultasCount(mamInterconsultas, dataInicial, dataFinal, consultoria, excluidos);
	}
	/**
	 * Grava atualização de gestão de interconsultas.
	 * 
	 * @param checkAvisadoAnterior
	 * @param checkAvisadoNovo
	 * @param foiAvisado
	 * @param checkExcluidoAnterior
	 * @param checkExcluidoNovo
	 * @param mamInterconsultas
	 * @throws ApplicationBusinessException
	 */
	public void gravarInterconsultas(MamInterconsultas mamInterconsultas) throws ApplicationBusinessException {
		 
		 AacConsultas consultaMarcada = null;
		 AacConsultas consultaRetorno = null;
		 
		 if (mamInterconsultas.getConsultaMarcada() != null && mamInterconsultas.getConsultaMarcada().getNumero() != null &&  mamInterconsultas.getConsultaRetorno() != null &&  mamInterconsultas.getConsultaRetorno().getNumero() != null) {          
			 throw new ApplicationBusinessException(GestaoInterconsultasRNExceptionCode.GESTAO_INTERCONSULTA_INT_OU_CONS_RETORNO);
			 
		 } else if (mamInterconsultas.getConsultaMarcada() != null && mamInterconsultas.getConsultaMarcada().getNumero() != null) {			 
		     List<AacConsultas> listaConsulta = aacConsultasDAO.pesquisarPorNumeroEProntuario(mamInterconsultas.getConsultaMarcada().getNumero(), mamInterconsultas.getPaciente().getProntuario());
		          
		     if (listaConsulta == null || listaConsulta.isEmpty()) {
		    	 throw new ApplicationBusinessException(GestaoInterconsultasRNExceptionCode.MAM_01745);
		     }
		     consultaMarcada = aacConsultasDAO.obterConsulta(mamInterconsultas.getConsultaMarcada().getNumero());
		     
		 } else if (mamInterconsultas.getConsultaRetorno() != null && mamInterconsultas.getConsultaRetorno().getNumero() != null) {
		  	 			
		  	 List<AacConsultas> listaConsulta = aacConsultasDAO.pesquisarPorNumeroEProntuario(mamInterconsultas.getConsultaRetorno().getNumero(), mamInterconsultas.getPaciente().getProntuario());
		          
		     if (listaConsulta == null || listaConsulta.isEmpty()) {
		    	 throw new ApplicationBusinessException(GestaoInterconsultasRNExceptionCode.MAM_01745);
		     }
		     consultaRetorno = aacConsultasDAO.obterConsulta(mamInterconsultas.getConsultaRetorno().getNumero());
		 }	
		 
	     MamInterconsultas entidadePersistente = mamInterconsultasDAO.obterPorChavePrimaria(mamInterconsultas.getSeq());
	     
	     //verifica constraint mam_ieo_ck12
	     this.verificarConstraintMamIeoCk12(entidadePersistente, consultaMarcada);
	     entidadePersistente.setDthrCriacao(mamInterconsultas.getDthrCriacao());
	     
	     if (mamInterconsultas.getEspecialidadeAdm() != null) {
	            AghEspecialidades agendaPersistente = aghEspecialidadesDAO.obterPorChavePrimaria(mamInterconsultas.getEspecialidadeAdm().getSeq());
	            if (agendaPersistente.getSeq() != null) {
	                   entidadePersistente.setEspecialidadeAdm(agendaPersistente);
	            } else {
	                   entidadePersistente.setEspecialidadeAdm(null);
	            }
	     } else {
	            entidadePersistente.setEspecialidadeAdm(null);
	     }
	     
	     entidadePersistente.setConsultaMarcada(consultaMarcada);
	     entidadePersistente.setConsultaRetorno(consultaRetorno);
	     if (consultaMarcada != null || consultaRetorno != null) {
	    	 entidadePersistente.setSituacao(DominioSituacaoInterconsultasPesquisa.M);
	    	 entidadePersistente.setDthrAvisaRetorno(null);
	     }
	     if (consultaMarcada == null && consultaRetorno == null) {
	    	 entidadePersistente.setSituacao(DominioSituacaoInterconsultasPesquisa.P);
	    	 entidadePersistente.setAvaliacao(DominioAvaliacaoInterconsulta.L);
	     }
	     mamInterconsultasDAO.atualizar(entidadePersistente);
	}
	
	/**
	 * Lança mensagem caso a constraint mam_ieo_ck12 não seja atendida.
	 */
	private void verificarConstraintMamIeoCk12(MamInterconsultas entidade, AacConsultas consultaMarcada) throws ApplicationBusinessException {
		if (consultaMarcada != null && consultaMarcada.getNumero() != null) {
			if (entidade.getAvaliacao().equals(DominioAvaliacaoInterconsulta.S)|| entidade.getAvaliacao().equals(DominioAvaliacaoInterconsulta.I)|| entidade.getAvaliacao().equals(DominioAvaliacaoInterconsulta.C)) {
				throw new ApplicationBusinessException(GestaoInterconsultasRNExceptionCode.CONSULTORIA_AVALIADA_SEM_INDICACAO_NOVA_CONSULTA);
			}
		}
		
		if (consultaMarcada == null || consultaMarcada.getNumero() == null) {
			if (entidade.getAvaliacao() == null) {				
				throw new ApplicationBusinessException(GestaoInterconsultasRNExceptionCode.CONSULTORIA_AVALIADA_SEM_INDICACAO_NOVA_CONSULTA);
			}
		}
	}

	/**
	 * GET's e SET's
	 */
	public MamInterconsultasDAO getMamInterconsultasDAO() {
		return mamInterconsultasDAO;
	}
	public void setMamInterconsultasDAO(MamInterconsultasDAO mamInterconsultasDAO) {
		this.mamInterconsultasDAO = mamInterconsultasDAO;
	}
	public AacConsultasDAO getAacConsultasDAO() {
		return aacConsultasDAO;
	}
	/**
	 *ORADB: MAMC_VER_CONSULTOR
	 * Estoria 40229
	 * @param espSeq
	 * @param espSeqAgenda
	 * @param eqpSeq
	 * @param cabEspSeq
	 * @param cabEspSeqAgenda
	 * @param cabEqpSeq
	 * @return
	 */
	public boolean mamcVerConsultor(MamInterconsultas interconsulta, MamConsultorAmbulatorio consultaAmbulatorio) {
		Short espSeq = null;
		Short espSeqAgenda = null;
		Integer eqpSeq = null;
		Short cabEspSeq = null;
		Short cabEspSeqAgenda = null;
		Integer cabEqpSeq = null;
		if (interconsulta.getEspecialidade() != null && interconsulta.getEspecialidade().getSeq() != null) {
			espSeq = interconsulta.getEspecialidade().getSeq();
		}
		if (interconsulta.getEspecialidadeAgenda() != null && interconsulta.getEspecialidadeAgenda().getEspecialidade() != null
				&& interconsulta.getEspecialidadeAgenda().getEspecialidade().getSeq() != null) {
			espSeqAgenda = interconsulta.getEspecialidade().getSeq();
		}
		if (interconsulta.getEquipe() != null && interconsulta.getEquipe().getSeq() != null) {
			eqpSeq = interconsulta.getEquipe().getSeq();
		}
		if (consultaAmbulatorio.getAghEspecialidadesByEspSeq() != null && consultaAmbulatorio.getAghEspecialidadesByEspSeq().getSeq() != null ) {
			cabEspSeq = consultaAmbulatorio.getAghEspecialidadesByEspSeq().getSeq();
		}
		if (consultaAmbulatorio.getAghEspecialidadesByEspSeqAgenda() != null && consultaAmbulatorio.getAghEspecialidadesByEspSeqAgenda().getSeq() != null ) {
			cabEspSeqAgenda = consultaAmbulatorio.getAghEspecialidadesByEspSeqAgenda().getSeq();
		}
		if (consultaAmbulatorio.getAghEquipes() != null && consultaAmbulatorio.getAghEquipes().getSeq() != null ) {
			cabEqpSeq = consultaAmbulatorio.getAghEquipes().getSeq();
		}
		return verificaMamcVerConsultor(espSeq, espSeqAgenda, eqpSeq, cabEspSeq,
				cabEspSeqAgenda, cabEqpSeq);		
	}

	private boolean verificaMamcVerConsultor(Short espSeq, Short espSeqAgenda,
			Integer eqpSeq, Short cabEspSeq, Short cabEspSeqAgenda,
			Integer cabEqpSeq) {
		if(cabEspSeq != null){
			if(cabEspSeqAgenda == null && cabEqpSeq == null){
				if(espSeq == cabEspSeq){
					return true;
				}
			}
			if(cabEspSeqAgenda != null && cabEqpSeq == null){
				if(espSeq == cabEspSeq && espSeqAgenda == cabEspSeqAgenda){
					return true;
				}
			}
			if(cabEspSeqAgenda != null && cabEqpSeq != null){
				if(espSeq == cabEspSeq && espSeqAgenda  == cabEspSeqAgenda && eqpSeq == cabEqpSeq){
					return true;
				}
			}
			if(cabEspSeqAgenda == null && cabEqpSeq != null){
				if(espSeq == cabEspSeq && eqpSeq == cabEqpSeq){
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * ORADB:RN_IEOP_DEL_CAIXA_P
	 * @param interconsulta
	 * Estoria:40229
	 */
	public void removeCaixaPostalCaixaPostalServidoresCaixaPostalAplicacaoParametroAplicacao(MamInterconsultas interconsulta){
		//TODO A relação das entidades  (AghCaixaPostalServidor e AghCaixaPostalAplicacao) não existe. Rever a consulta. 
		List<AghCaixaPostalServidor> aghCaixaPostalServidors = aghCaixaPostalServidorDAO.pesquisarAghCaixaPostalServidor(interconsulta);
		for(AghCaixaPostalServidor aghCaixaPostalServidor :aghCaixaPostalServidors){			
			aghParametroAplicacaoDAO.removerAghParamtroAplicaoPorCaixaPostalServidoresSeq(aghCaixaPostalServidor.getId().getCxtSeq());			
			aghCaixaPostalAplicacaoDAO.removerAghCaixaPostalAplicacaoPorCaixaPostalServidoresSeq(aghCaixaPostalServidor.getId().getCxtSeq());
			aghCaixaPostalServidorDAO.removerAghCaixaPostalServidorPorCaixaPostalServidoresSeq(aghCaixaPostalServidor.getId().getCxtSeq());
			aghCaixaPostalDAO.removerAghCaixaPostalPorCaixaPostalServidoresSeq(aghCaixaPostalServidor.getId().getCxtSeq());
		}
	}
	
	/**
	 * 
	 * @param interconsulta
	 * Estoria: 40229
	 * ORADB:RN_IEOP_INS_CAIXA_P
	 * @throws ApplicationBusinessException 
	 */
	public void adicionaCaixaPostalCaixaPostalAplicacaoCaixaPostalServidoresParametroAplicacoes(MamInterconsultas interconsulta) throws ApplicationBusinessException{
		//Executar C16
		String DescricaoCidCapitalizada =getAacConsultasDAO().obterDescricaoCidCapitalizada(interconsulta.getConsulta().getNumero());
		//Executar C17
		String vMensagem = execultaConsulta(interconsulta,
				DescricaoCidCapitalizada);
		// Inico adiciona aghCaixaPostal
		AghCaixaPostal aghCaixaPostal = adicionaCaixaPostal(vMensagem);
		//fim adiciona caixa postal		
		//criar caixa postal aplicação
		AghCaixaPostalAplicacao aghCaixaPostalAplicacao = criaCaixaPostalAplicacao(aghCaixaPostal);
		//adiciona Parametro aplicaçao
		adicionaParametroAplicacaoOrdem1(interconsulta, aghCaixaPostalAplicacao);
		// adiciona parametro aplicacao
		//reutilizando variavel	
		adicionaParametroAplicacaoOrdem2(interconsulta, aghCaixaPostalAplicacao);		
		//adiciona parametro aplicacao		
		//reutilizando variavel	
		adicionaParametroAplicacaoOrdem3(aghCaixaPostalAplicacao);		
//		Caso a inclusão gere exceção, apresentar a mensagem MAM_05099
		//reutilizando variavel	
		adicionaParametroAplicacaoOrdem4(aghCaixaPostalAplicacao);
		//adiciona caixa postal servidor
		adicionaCaixaPostalServidor(interconsulta);
//		    Obs.: Deve ser criado o método de persistência na classe "AghCaixaPostalServidorDAO" 
//		    Caso a inclusão gere exceção, apresentar a mensagem MAM_05094
	}
	private String execultaConsulta(MamInterconsultas interconsulta,
			String DescricaoCidCapitalizada) {
		aghEspecialidadesDAO.obterNomeEspecialidadePorEspSeq(interconsulta.getEspecialidade().getSeq());
		StringBuilder vMensagem = new StringBuilder(100);
		
		vMensagem.append("Resposta da consultoria ambulatorial solicitada em ")
			.append(interconsulta.getDthrCriacao())
			.append(" para ")
			.append(interconsulta.getEspecialidade().getNomeEspecialidade())
			.append(", ")
			.append(DescricaoCidCapitalizada)
			.append(" \n")
			.append(" Avaliação: ")
			.append(interconsulta.getAvaliacao().getDescricao());
		if(interconsulta.getConsulta().getNumero() != null){
			vMensagem.append(getAacConsultasDAO().mensagemInterconsultaDataEspecialidade(interconsulta.getConsulta().getNumero()));
		}
		else if(interconsulta.getAvaliacao().equals(DominioAvaliacaoInterconsulta.S)||interconsulta.getAvaliacao().equals(DominioAvaliacaoInterconsulta.I)||interconsulta.getAvaliacao().equals(DominioAvaliacaoInterconsulta.C)){
			vMensagem.append(' ')
				.append(interconsulta.getParecerConsultor());
		}
		if(interconsulta.getDthrAvaliacao()!=null){
			vMensagem.append(" Resposta em ");
			vMensagem.append(interconsulta.getDthrAvaliacao());
			vMensagem.append(" por ");
			vMensagem.append(interconsulta.getServidorAvalia().getPessoaFisica().getNome());
		}
		return vMensagem.toString();
		
	}
	private void adicionaCaixaPostalServidor(MamInterconsultas interconsulta)throws ApplicationBusinessException {
		AghCaixaPostalServidor aghCaixaPostalServidor = new AghCaixaPostalServidor();
		AghCaixaPostalServidorId caixaPostalServidorId = new AghCaixaPostalServidorId();
		RapServidores servidor = new RapServidores();
		RapServidoresId rapServidoresId = new RapServidoresId();//set Matricula e V=vinCodigo
		rapServidoresId.setMatricula(interconsulta.getServidorValida().getId().getMatricula());
		rapServidoresId.setVinCodigo(interconsulta.getServidorValida().getId().getVinCodigo());
		servidor.setId(rapServidoresId);
		caixaPostalServidorId.setServidor(servidor);
		
		aghCaixaPostalServidor.setId(caixaPostalServidorId);
		aghCaixaPostalServidor.setDthrLida(null);
		aghCaixaPostalServidor.setDthrExcluida(null);
		aghCaixaPostalServidor.setMotivoExcluida(null);
		aghCaixaPostalServidor.setSituacao(DominioSituacaoCxtPostalServidor.N);
		aghCaixaPostalServidor.setDthrExcluirDefinitiva(null);
		try {  
			aghCaixaPostalServidorDAO.persistir(aghCaixaPostalServidor);
		} catch (Exception e) {
			throw new ApplicationBusinessException(GestaoInterconsultasRNExceptionCode.MAM_05094, Severity.ERROR);
		}
	}
	private void adicionaParametroAplicacaoOrdem4(AghCaixaPostalAplicacao aghCaixaPostalAplicacao)throws ApplicationBusinessException {
		AghParametroAplicacaoId aghParametroAplicacaoId;
		AghParametroAplicacao aghParametroAplicacao;
		aghParametroAplicacao = new AghParametroAplicacao();					
		aghParametroAplicacaoId = new AghParametroAplicacaoId();
		aghParametroAplicacaoId.setCxaCxtSeq(aghCaixaPostalAplicacao.getId().getCxtSeq());
		aghParametroAplicacaoId.setCxaAplicacaoCodigo(MAMF_LISTA_CONS_AMB);	
		aghParametroAplicacaoId.setSeqp((short)4);
		aghParametroAplicacao.setId(aghParametroAplicacaoId);
		aghParametroAplicacao.setParametros("N");
		aghParametroAplicacao.setNome("P_NEW_SESSION");
		aghParametroAplicacao.setOrdem((short)4);
		aghParametroAplicacao.setTipoNomeParam(DominioTipoNomeParam.P);
		aghParametroAplicacao.setAghCaixaPostalAplicacao(aghCaixaPostalAplicacao);
//		Caso a inclusão gere exceção, apresentar a mensagem MAM_05099
		try {
			aghParametroAplicacaoDAO.persistir(aghParametroAplicacao);			
		} catch (Exception e) {
			throw new ApplicationBusinessException(GestaoInterconsultasRNExceptionCode.MAM_05099, Severity.ERROR);
		}
	}
	private void adicionaParametroAplicacaoOrdem3(AghCaixaPostalAplicacao aghCaixaPostalAplicacao)throws ApplicationBusinessException {
		AghParametroAplicacaoId aghParametroAplicacaoId;
		AghParametroAplicacao aghParametroAplicacao;
		aghParametroAplicacao = new AghParametroAplicacao();
		aghParametroAplicacaoId = new AghParametroAplicacaoId();
		aghParametroAplicacaoId.setCxaCxtSeq(aghCaixaPostalAplicacao.getId().getCxtSeq());
		aghParametroAplicacaoId.setCxaAplicacaoCodigo(MAMF_LISTA_CONS_AMB);	
		aghParametroAplicacaoId.setSeqp((short)3);
		aghParametroAplicacao.setId(aghParametroAplicacaoId);//adiciona  aplicacao codigo e seqp
		aghParametroAplicacao.setParametros("Y");
		aghParametroAplicacao.setNome("P_OPEN_FORM");
		aghParametroAplicacao.setOrdem((short)3);
		aghParametroAplicacao.setTipoNomeParam(DominioTipoNomeParam.P);
		aghParametroAplicacao.setAghCaixaPostalAplicacao(aghCaixaPostalAplicacao);
		try {
			aghParametroAplicacaoDAO.persistir(aghParametroAplicacao);			
		} catch (Exception e) {
			throw new ApplicationBusinessException(GestaoInterconsultasRNExceptionCode.MAM_05099, Severity.ERROR);
		}
	}
	private void adicionaParametroAplicacaoOrdem2(MamInterconsultas interconsulta,AghCaixaPostalAplicacao aghCaixaPostalAplicacao)throws ApplicationBusinessException {
		AghParametroAplicacaoId aghParametroAplicacaoId;
		AghParametroAplicacao aghParametroAplicacao;
		aghParametroAplicacao = new AghParametroAplicacao();
		aghParametroAplicacaoId = new AghParametroAplicacaoId();
		aghParametroAplicacaoId.setCxaCxtSeq(aghCaixaPostalAplicacao.getId().getCxtSeq());
		aghParametroAplicacaoId.setCxaAplicacaoCodigo(MAMF_LISTA_CONS_AMB);	
		aghParametroAplicacaoId.setSeqp((short)2);
		aghParametroAplicacao.setId(aghParametroAplicacaoId);//adiciona  aplicacao codigo e seqp
		aghParametroAplicacao.setParametros(interconsulta.getSeq().toString());
		aghParametroAplicacao.setNome("GLOBAL.CG$IEO_SEQ");
		aghParametroAplicacao.setOrdem((short)2);
		aghParametroAplicacao.setTipoNomeParam(DominioTipoNomeParam.G);
		aghParametroAplicacao.setAghCaixaPostalAplicacao(aghCaixaPostalAplicacao);
//		Caso a inclusão gere exceção, apresentar a mensagem MAM_05099
		try {
			aghParametroAplicacaoDAO.persistir(aghParametroAplicacao);			
		} catch (Exception e) {
			throw new ApplicationBusinessException(GestaoInterconsultasRNExceptionCode.MAM_05099, Severity.ERROR);
		}
	}
	private void adicionaParametroAplicacaoOrdem1(MamInterconsultas interconsulta,AghCaixaPostalAplicacao aghCaixaPostalAplicacao)throws ApplicationBusinessException {		
		AghParametroAplicacaoId aghParametroAplicacaoId = new AghParametroAplicacaoId();
		aghParametroAplicacaoId.setCxaAplicacaoCodigo(MAMF_LISTA_CONS_AMB);	
		aghParametroAplicacaoId.setSeqp((short)1);
		aghParametroAplicacaoId.setCxaCxtSeq(aghCaixaPostalAplicacao.getId().getCxtSeq());
		AghParametroAplicacao aghParametroAplicacao = new AghParametroAplicacao();
		aghParametroAplicacao.setId(aghParametroAplicacaoId);//adiciona  aplicacao codigo e seqp
		aghParametroAplicacao.setParametros(interconsulta.getPaciente().getCodigo().toString());
		aghParametroAplicacao.setNome("global.aip$prntol_pac_codigo");
		aghParametroAplicacao.setOrdem((short)1);
		aghParametroAplicacao.setTipoNomeParam(DominioTipoNomeParam.G);
		aghParametroAplicacao.setAghCaixaPostalAplicacao(aghCaixaPostalAplicacao);
		try {
			aghParametroAplicacaoDAO.persistir(aghParametroAplicacao);			
		} catch (Exception e) {
			throw new ApplicationBusinessException(GestaoInterconsultasRNExceptionCode.MAM_05099, Severity.ERROR);
		}
	}
	private AghCaixaPostalAplicacao criaCaixaPostalAplicacao(AghCaixaPostal aghCaixaPostal) throws ApplicationBusinessException {
		AghCaixaPostalAplicacaoId caixaPostalAplicacao = new AghCaixaPostalAplicacaoId();
		caixaPostalAplicacao.setAplicacaoCodigo(MAMF_LISTA_CONS_AMB);
		caixaPostalAplicacao.setCxtSeq(aghCaixaPostal.getSeq());
		AghCaixaPostalAplicacao aghCaixaPostalAplicacao = new AghCaixaPostalAplicacao();
		aghCaixaPostalAplicacao.setId(caixaPostalAplicacao);//adiciona 
		aghCaixaPostalAplicacao.setOrdemChamada((short) 1);
		try {
			aghCaixaPostalAplicacaoDAO.persistir(aghCaixaPostalAplicacao);	
		} catch (Exception e) {
			throw new ApplicationBusinessException(GestaoInterconsultasRNExceptionCode.MAM_05092, Severity.ERROR);
		}
		return aghCaixaPostalAplicacao;
	}
	private AghCaixaPostal adicionaCaixaPostal(String vMensagem)throws ApplicationBusinessException {
		AghCaixaPostal aghCaixaPostal = new AghCaixaPostal();
		Date dataAtual = new Date();		
		aghCaixaPostal.setDthrInicio(dataAtual);
		aghCaixaPostal.setDthrFim(null);
		aghCaixaPostal.setTipoMensagem(DominioTipoMensagemExame.A);
		aghCaixaPostal.setAcaoObrigatoria(DominioSimNao.N.isSim());
		aghCaixaPostal.setMensagem(vMensagem);
		aghCaixaPostal.setCriadoEm(dataAtual);
		aghCaixaPostal.setFormaIdentificacao(DominioFormaIdentificacaoCaixaPostal.E);
		aghCaixaPostal.setNomeRotina(null);
		try {
			aghCaixaPostalDAO.persistir(aghCaixaPostal);	
		} catch (Exception e) {
			throw new ApplicationBusinessException(GestaoInterconsultasRNExceptionCode.MAM_05091, Severity.ERROR);
		}
		return aghCaixaPostal;
	}
	/**
	 * 
	 * @param interconsulta
	 * Estoria: 40229
	 * ORADB:MAMC_PROF_CONSULTOR
	 */
	public boolean verificaRetornoConsultapPesquisarMamConsultorAmbulatorioSeqPorServidor(MamInterconsultas interconsulta){
		//executa C9 part1
		List<MamConsultorAmbulatorio> listaMamConsultorAmbulatorio = mamConsultorAmbulatorioDAO.pesquisarMamConsultorAmbulatorioSeqPorServidor(interconsulta.getServidorValida()); 
		//segunda parte da C9 verifica se MAMC_VER_CONSULTOR = true
		if(!aplicaRegraNegocioNomedaregraConsultas(listaMamConsultorAmbulatorio,interconsulta).isEmpty()){
			return true;
		}
		return false;
	}
	//segunda parte da C9 e C10 verifica se MAMC_VER_CONSULTOR = true
	public List<MamConsultorAmbulatorio> aplicaRegraNegocioNomedaregraConsultas(List<MamConsultorAmbulatorio> listaMamConsultorAmbulatorio,
			MamInterconsultas interconsulta){
		for(MamConsultorAmbulatorio mamConsultorAmbulatorio : listaMamConsultorAmbulatorio){
			if(!this.mamcVerConsultor(interconsulta,mamConsultorAmbulatorio)){	//remove mamConsultorAmbulatorio da lista se diferente de true
				listaMamConsultorAmbulatorio.remove(mamConsultorAmbulatorio);
			}
		}
		return listaMamConsultorAmbulatorio;
	}
	/**
	 * 
	 * @param interconsulta
	 * Estoria:40229
	 * ORADB:RN_IEOP_ATU_SIT_AVAL
	 */
	public void alteraAvaliacaoInterconsulta(MamInterconsultas interconsulta){ 
		//executa C10 part1
		List<MamConsultorAmbulatorio> listaMamConsultorAmbulatorio = mamConsultorAmbulatorioDAO.pesquisarMamConsultorAmbulatorioPorEspecialidade(interconsulta.getEspecialidade().getEpcSeq());		
		if(!aplicaRegraNegocioNomedaregraConsultas(listaMamConsultorAmbulatorio,interconsulta).isEmpty()){
				interconsulta.setAvaliacao(DominioAvaliacaoInterconsulta.N);
		}else{
			interconsulta.setAvaliacao(DominioAvaliacaoInterconsulta.L);			
		}
	}
	/**
	 * ORADB: MAMT_IEO_BRU, MAMT_IEO_BSU
	 * @throws ApplicationBusinessException 
	 */
	private void preAtualizarInterconsulta(MamInterconsultas oldMamInterconsultas, MamInterconsultas interconsulta) throws ApplicationBusinessException {
		if(oldMamInterconsultas.getPendente().equals(DominioIndPendenteAmbulatorio.V)){
			if(oldMamInterconsultas.getEspecialidade() != null && !oldMamInterconsultas.getEspecialidade().getSeq().equals(interconsulta.getEspecialidade().getSeq()) ||
					oldMamInterconsultas.getEquipe() != null && !oldMamInterconsultas.getEquipe().getSeq().equals(interconsulta.getEquipe().getSeq()) ||
					!oldMamInterconsultas.getObservacao().equals(interconsulta.getObservacao()) ||
					oldMamInterconsultas.getServidorResponsavel() != null && 
					!oldMamInterconsultas.getServidorResponsavel().equals(interconsulta.getServidorResponsavel())){
				if(interconsulta.getDigitado().equals(DominioSimNao.N.toString())){
					throw new ApplicationBusinessException(GestaoInterconsultasRNExceptionCode.MAM_00605, Severity.INFO);
				}
			}
		}
		 /* Verifica se a consulta origem da interconsulta não é consulta de projeto de pesquisa */ //OK!!
		verificaConsultaOrigemInterconsultaNaoPesquisa(oldMamInterconsultas, interconsulta);
		 /* Data da consulta de origem não pode ser futura */ //OK!!
		dataConsultaOrigem(oldMamInterconsultas, interconsulta);		
		/* Verifica se já existe consulta marcada para mesma especialidade e mesmo paciente */ //OK!!
		verificaConsultaMarcadaEspecialidadePaciente(oldMamInterconsultas, interconsulta);
		 /* Verifica se o paciente da consulta marcada é o mesmo da consulta de origem */ //OK!!
		verificaConsultaMarcadaOrigem(oldMamInterconsultas, interconsulta);
		/* Atualiza a especialidade administrativa */ //OK!!
		atualizaEspecialidadeAdm(oldMamInterconsultas, interconsulta);
		/* Conforme a situação, alimentar ou não os campos: */
	    /* dthr e servidor marcada, dthr e servidor avisada */ //OK!!
		situacaoAlimentarCampos(oldMamInterconsultas, interconsulta);  //OK!!
		/* Seta os campos data e hora e servidor avaliação */ //OK!!
		setaDataHoraServidorAvaliacao(oldMamInterconsultas, interconsulta);
		/* Seta o servidor que visualiza a resposta pela primeira vez */ //OK!!
		setaServidorVisualiza(oldMamInterconsultas, interconsulta);
		/* Seta os campos data e hora e servidor que marca a consulta de retorno */ //OK!!
		setaDataHoraServidorConsultaRetorno(oldMamInterconsultas, interconsulta);
		/* Caso o consultor mude a resposta o servidor que visualiza a resposta pela primeira vez deve ser inicializado */ //OK!!
		consultorMudarResposta(oldMamInterconsultas, interconsulta);
		/* Seta os campos data e hora e servidor que avisa o paciente sobre a consulta de retorno */ //OK!!
		setaDataHoraServidorAvisaPacienteRetorno(oldMamInterconsultas, interconsulta);
		/* Caso a solicitação seja atendida sem avaliação, atualizar o parecer do consultor para Agendamento pela Secretária */ //OK!! C9
		solicitacaoSemAvaliacaoAtualizarAgendamento(oldMamInterconsultas, interconsulta);
		/* Solicitante recebe na sua caixa postal aviso da avaliação feita pelo consultor */
		avisoAvaliacaoFeitoConsultor(oldMamInterconsultas, interconsulta);
		/* Atualizar o parecer para Agendamento pela Secretária caso não exista consultor para a especialidade/agenda/equipe */
		atualizarAgendamento(oldMamInterconsultas, interconsulta);
	}
	private void atualizarAgendamento(MamInterconsultas oldMamInterconsultas, MamInterconsultas interconsulta) {
		if (oldMamInterconsultas.getEspecialidade() != null && interconsulta.getEspecialidade() != null && oldMamInterconsultas.getEspecialidadeAgenda() != null && interconsulta.getEspecialidadeAgenda() != null && oldMamInterconsultas.getEquipe() != null && interconsulta.getEquipe() != null){
			if(!oldMamInterconsultas.getEspecialidade().getSeq().equals(interconsulta.getEspecialidade().getSeq()) || !oldMamInterconsultas.getEspecialidadeAgenda().getSeq().equals(interconsulta.getEspecialidadeAgenda().getSeq()) || !oldMamInterconsultas.getEquipe().getSeq().equals(interconsulta.getEquipe().getSeq())){		
				alteraAvaliacaoInterconsulta(interconsulta);			 
			}			
		}
	}
	private void avisoAvaliacaoFeitoConsultor(MamInterconsultas oldMamInterconsultas, MamInterconsultas interconsulta) throws ApplicationBusinessException {
		if (oldMamInterconsultas.getAvaliacao() != null && interconsulta.getAvaliacao() != null && oldMamInterconsultas.getParecerConsultor() != null && interconsulta.getParecerConsultor() != null){
			if(!oldMamInterconsultas.getAvaliacao().equals(interconsulta.getAvaliacao()) ||!oldMamInterconsultas.getParecerConsultor().equals(interconsulta.getParecerConsultor())){
				//TODO A relação das entidades  (AghCaixaPostalServidor e AghCaixaPostalAplicacao) não existe. Rever a consulta.
				//removeCaixaPostalCaixaPostalServidoresCaixaPostalAplicacaoParametroAplicacao(interconsulta);
				if(interconsulta.getAvaliacao().equals(DominioAvaliacaoInterconsulta.S) || interconsulta.getAvaliacao().equals(DominioAvaliacaoInterconsulta.I) || interconsulta.getAvaliacao().equals(DominioAvaliacaoInterconsulta.C) ||interconsulta.getAvaliacao().equals(DominioAvaliacaoInterconsulta.M) || interconsulta.getAvaliacao().equals(DominioAvaliacaoInterconsulta.L)){
					adicionaCaixaPostalCaixaPostalAplicacaoCaixaPostalServidoresParametroAplicacoes(interconsulta);
				}
			}			
		}
	}
	private void solicitacaoSemAvaliacaoAtualizarAgendamento(MamInterconsultas oldMamInterconsultas, MamInterconsultas interconsulta) throws ApplicationBusinessException {
				
		if(!oldMamInterconsultas.getSituacao().equals(interconsulta.getSituacao())){
			if(interconsulta.getSituacao().equals(DominioSituacaoInterconsultasPesquisa.M) || interconsulta.getSituacao().equals(DominioSituacaoInterconsultasPesquisa.A) && interconsulta.getAvaliacao().equals(DominioAvaliacaoInterconsulta.N)){
				if(verificaRetornoConsultapPesquisarMamConsultorAmbulatorioSeqPorServidor(interconsulta)){ 
					interconsulta.setAvaliacao(DominioAvaliacaoInterconsulta.M);
					interconsulta.setDthrMarcada(new Date());
					interconsulta.setServidorAvalia(registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado()).getServidor());					
				} else {
					interconsulta.setAvaliacao(DominioAvaliacaoInterconsulta.L);
				}
			}
		}
	}
	private void setaDataHoraServidorAvisaPacienteRetorno(MamInterconsultas oldMamInterconsultas, MamInterconsultas interconsulta) throws ApplicationBusinessException {
		
		if (oldMamInterconsultas.getDthrAvisaRetorno() != null && interconsulta.getDthrAvisaRetorno() != null){
			if(!oldMamInterconsultas.getDthrAvisaRetorno().equals(interconsulta.getDthrAvisaRetorno())){
				if(interconsulta.getDthrAvisaRetorno() != null){
					interconsulta.setServidorRetorno(registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado()).getServidor());
				} else {
					interconsulta.setServidorAvisaRetorno(null);				
				}
			}			
		}
	}
	private void consultorMudarResposta(MamInterconsultas oldMamInterconsultas, MamInterconsultas interconsulta) throws ApplicationBusinessException {
		
		if (oldMamInterconsultas.getParecerConsultor() != null && interconsulta.getParecerConsultor() != null){
			if(!oldMamInterconsultas.getParecerConsultor().equals(interconsulta.getParecerConsultor())){
				interconsulta.setDthrVisualizaResposta(null);
				interconsulta.setServidorVisualiza(registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado()).getServidor());
			}			
		}
	}
	private void setaDataHoraServidorConsultaRetorno(MamInterconsultas oldMamInterconsultas, MamInterconsultas interconsulta) throws ApplicationBusinessException {
		
		if (oldMamInterconsultas.getConsultaRetorno() != null && interconsulta.getConsultaRetorno() != null){
			if(!oldMamInterconsultas.getConsultaRetorno().getNumero().equals(interconsulta.getConsultaRetorno().getNumero())){
				if(interconsulta.getConsultaRetorno() != null && interconsulta.getConsultaRetorno().getNumero() != null){
					interconsulta.setDthrMarcacaoRetorno(new Date());
					interconsulta.setServidorRetorno(registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado()).getServidor());
				} else{
					interconsulta.setDthrMarcacaoRetorno(null);
					interconsulta.setServidorAvisada(null);					
				}
			}			
		}
	}
	private void setaServidorVisualiza(MamInterconsultas oldMamInterconsultas, MamInterconsultas interconsulta) throws ApplicationBusinessException {
		
		if (oldMamInterconsultas.getDthrVisualizaResposta() != null && interconsulta.getDthrVisualizaResposta() != null){
			if(!oldMamInterconsultas.getDthrVisualizaResposta().equals(interconsulta.getDthrVisualizaResposta())){
				if(interconsulta.getDthrVisualizaResposta() != null){
					interconsulta.setServidorAvisada(registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado()).getServidor());
				}
			}			
		}		
	}
	private void setaDataHoraServidorAvaliacao(MamInterconsultas oldMamInterconsultas, MamInterconsultas interconsulta) throws ApplicationBusinessException {
		if (oldMamInterconsultas.getAvaliacao() != null && interconsulta.getAvaliacao() != null && oldMamInterconsultas.getParecerConsultor() != null && interconsulta.getParecerConsultor() != null){
			if(!oldMamInterconsultas.getAvaliacao().equals(interconsulta.getAvaliacao()) || !oldMamInterconsultas.getParecerConsultor().equals(interconsulta.getParecerConsultor())){
				if(!interconsulta.getAvaliacao().equals(DominioAvaliacaoInterconsulta.N)){
					interconsulta.setDthrMarcada(new Date());
					interconsulta.setServidorAvalia(registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado()).getServidor());
					if(interconsulta.getAvaliacao().equals(DominioAvaliacaoInterconsulta.S) || interconsulta.getAvaliacao().equals(DominioAvaliacaoInterconsulta.I) || interconsulta.getAvaliacao().equals(DominioAvaliacaoInterconsulta.O)){
						interconsulta.setSituacao(DominioSituacaoInterconsultasPesquisa.O);
					} else {
						interconsulta.setParecerConsultor(null);
					}
					if(interconsulta.getAvaliacao().equals(DominioAvaliacaoInterconsulta.L)){
						interconsulta.setSituacao(DominioSituacaoInterconsultasPesquisa.P);
					}
				} else {
					interconsulta.setDthrAvaliacao(null);
					interconsulta.setServidorAvalia(null);
					interconsulta.setParecerConsultor(null);
					interconsulta.setSituacao(DominioSituacaoInterconsultasPesquisa.P);
				}
			}			
		}				
	}
	private void situacaoAlimentarCampos(MamInterconsultas oldMamInterconsultas, MamInterconsultas interconsulta) throws ApplicationBusinessException {
		if(!oldMamInterconsultas.getSituacao().equals(interconsulta.getSituacao())){
			if(interconsulta.getSituacao().equals(DominioSituacaoInterconsultasPesquisa.P)){
				interconsulta.setServidorMarcada(null);
				interconsulta.setDthrMarcada(null);
				interconsulta.setServidorAvisada(null);
				interconsulta.setDthrAvisada(null);
			}
			if(interconsulta.getSituacao().equals(DominioSituacaoInterconsultasPesquisa.M) ||(oldMamInterconsultas.getConsultaMarcada() != null && oldMamInterconsultas.getConsultaMarcada().getNumero() != null && interconsulta.getConsultaMarcada() != null && interconsulta.getConsultaMarcada().getNumero() != null)){
				interconsulta.setServidorMarcada(registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado()).getServidor());
				interconsulta.setDthrMarcada(new Date());				
				interconsulta.setServidorAvisada(null);
				interconsulta.setDthrAvisada(null);				
			}
			if(interconsulta.getSituacao().equals(DominioSituacaoInterconsultasPesquisa.A)){
				interconsulta.setServidorAvisada(registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado()).getServidor());
				interconsulta.setDthrMarcada(new Date());
			}
		}
	}
	private void atualizaEspecialidadeAdm(MamInterconsultas oldMamInterconsultas, MamInterconsultas interconsulta) { 
		if(!oldMamInterconsultas.getEspecialidade().getSeq().equals(interconsulta.getEspecialidade().getSeq()) ||
			oldMamInterconsultas.getEspecialidadeAgenda() != null &&
			interconsulta.getEspecialidadeAgenda() != null &&
			!oldMamInterconsultas.getEspecialidadeAgenda().getSeq().equals(interconsulta.getEspecialidadeAgenda().getSeq())){
			if(interconsulta.getEspecialidadeAgenda().getSeq() != null){
				interconsulta.setEspecialidadeAdm(interconsulta.getEspecialidadeAgenda());
			} else {
				interconsulta.setEspecialidadeAdm(interconsulta.getEspecialidade());
			}
		}
	}
	private void verificaConsultaMarcadaOrigem(MamInterconsultas oldMamInterconsultas, MamInterconsultas interconsulta) throws ApplicationBusinessException {
		if (oldMamInterconsultas.getConsultaMarcada() != null && interconsulta.getConsultaMarcada() != null){
			if(!oldMamInterconsultas.getConsultaMarcada().getNumero().equals(interconsulta.getConsultaMarcada().getNumero())){
				if(interconsulta.getConsultaMarcada().getNumero() != null){				
					Integer retorno = getAacConsultasDAO().obterNumeroConsultaPacCodigoPorNumero(interconsulta.getConsulta().getNumero());
					if (retorno != null && !retorno.equals(interconsulta.getPaciente().getCodigo())){
						throw new ApplicationBusinessException(GestaoInterconsultasRNExceptionCode.MAM_01694, Severity.INFO);
					}else if(interconsulta.getAvaliacao() != null && 
							interconsulta.getAvaliacao().equals(DominioAvaliacaoInterconsulta.M)){
						interconsulta.setAvaliacao(DominioAvaliacaoInterconsulta.N);
					}				
				}
			}
		}			
	}
	private void verificaConsultaMarcadaEspecialidadePaciente(MamInterconsultas oldMamInterconsultas, MamInterconsultas interconsulta) throws ApplicationBusinessException {
		if (oldMamInterconsultas.getEspecialidade() != null && interconsulta.getEspecialidade() != null){
			if(!oldMamInterconsultas.getEspecialidade().getSeq().equals(interconsulta.getEspecialidade().getSeq())){
			if(aacGradeAgendamenConsultasDAO.verificaInterconsultaMarcadaPorEspecialidadeSeqPacienteCodigo(interconsulta.getPaciente().getCodigo(),interconsulta.getEspecialidade().getSeq())){
					throw new ApplicationBusinessException(GestaoInterconsultasRNExceptionCode.MAM_01638, Severity.INFO);
				}
			}			
		}
	}
	private void dataConsultaOrigem(MamInterconsultas oldMamInterconsultas, MamInterconsultas interconsulta) throws ApplicationBusinessException {
		if(!oldMamInterconsultas.getConsulta().getNumero().equals(interconsulta.getConsulta().getNumero())){
			Date retorno = getAacConsultasDAO().obterAacConsultasDataConsultaPorNumero(interconsulta.getConsulta().getNumero());		
			if(retorno.after(new Date())){
				throw new ApplicationBusinessException(GestaoInterconsultasRNExceptionCode.MAM_02915, Severity.INFO);
			}
		}
	}
	private void verificaConsultaOrigemInterconsultaNaoPesquisa(MamInterconsultas oldMamInterconsultas, MamInterconsultas interconsulta) throws ApplicationBusinessException {
		if(interconsulta.getConsulta().getNumero() != null && !oldMamInterconsultas.getConsulta().getNumero().equals(interconsulta.getConsulta().getNumero())){
			Integer seqPesquisa = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_PGD_SEQ_PESQUISA).getVlrNumerico().intValue();
			if(getAacConsultasDAO().verificaConsultaPorNumeroFormaAgendamento(
					interconsulta.getConsulta().getNumero(), 
					seqPesquisa)){
				throw new ApplicationBusinessException(GestaoInterconsultasRNExceptionCode.MAM_03429, Severity.INFO);
			}
		}
	}
	/**
	 * ORADB: MAMT_IEO_ASU, MAMT_IEO_ARU
	 * @throws ApplicationBusinessException 
	 */
	private void posAtualizarInterconsulta(MamInterconsultas oldMamInterconsultas, MamInterconsultas newMamInterconsultas ) throws ApplicationBusinessException {
		executarEnforcerInterconsultas(oldMamInterconsultas, newMamInterconsultas, DominioOperacaoBanco.UPD);
		if(habilitadoParaJournalling(oldMamInterconsultas, newMamInterconsultas)){
			inserirJournal(oldMamInterconsultas, DominioOperacoesJournal.UPD);
		}
	}
	/**
	 * ORADB Trigger MAMT_IEO_ARU <br/>
	 * complementada pelo método habilitarParaJournalling()
	 * @param oldMamInterconsultas
	 * @param operacaoJournal
	 */
	public void inserirJournal(MamInterconsultas oldMamInterconsultas, DominioOperacoesJournal operacaoJournal){
		final MamInterconsultaJn journalObject = BaseJournalFactory.getBaseJournal(operacaoJournal, MamInterconsultaJn.class, servidorLogadoFacade.obterServidorLogado().getUsuario());
		journalObject.setAvaliacao(oldMamInterconsultas.getAvaliacao().toString());
		journalObject.setConNumero(oldMamInterconsultas.getConsulta().getNumero());
		journalObject.setDthrAvaliacao(oldMamInterconsultas.getDthrAvaliacao());
		journalObject.setDthrAvisada(oldMamInterconsultas.getDthrAvisada());
		journalObject.setDthrAvisaRetorno(oldMamInterconsultas.getDthrAvisaRetorno());
		journalObject.setDthrConhecimentoConsultor(oldMamInterconsultas.getDthrConhecimentoConsultor());
		journalObject.setDthrCriacao(oldMamInterconsultas.getDthrCriacao());
		journalObject.setDthrMarcacaoRetorno(oldMamInterconsultas.getDthrMarcacaoRetorno());
		journalObject.setDthrMarcada(oldMamInterconsultas.getDthrMarcada());
		journalObject.setDthrVisualizaResposta(oldMamInterconsultas.getDthrVisualizaResposta());
		journalObject.setParecerConsultor(oldMamInterconsultas.getParecerConsultor());
		journalObject.setPrioridade(oldMamInterconsultas.getPrioridadeAprovada().toString());
		journalObject.setSeq(oldMamInterconsultas.getSeq());
		interconsultaJnDAO.persistir(journalObject);
	}
	/**
	 * Parte da Trigger MAMT_IEO_ARU
	 * @param oldMamInterconsultas
	 * @param operacaoJournal
	 * @return boolean
	 */	
	public boolean habilitadoParaJournalling(MamInterconsultas oldMamInterconsultas, MamInterconsultas newMamInterconsultas){
		if(oldMamInterconsultas.getAvaliacao() != newMamInterconsultas.getAvaliacao() ||oldMamInterconsultas.getPrioridadeAprovada() != newMamInterconsultas.getPrioridadeAprovada() ||oldMamInterconsultas.getConsultaMarcada() != null && newMamInterconsultas.getConsultaMarcada() != null &&
				oldMamInterconsultas.getConsultaMarcada().getNumero() != newMamInterconsultas.getConsultaMarcada().getNumero() ||
				oldMamInterconsultas.getEspecialidadeAdm().getSeq() != newMamInterconsultas.getEspecialidadeAdm().getSeq() ||
				(newMamInterconsultas.getDthrMarcada() != null && newMamInterconsultas.getDthrMarcada() != oldMamInterconsultas.getDthrMarcada()) ||
				(newMamInterconsultas.getDthrAvaliacao() != null && newMamInterconsultas.getDthrAvaliacao() != oldMamInterconsultas.getDthrAvaliacao()) ||
				(newMamInterconsultas.getDthrAvisada() != null && newMamInterconsultas.getDthrAvisada() != oldMamInterconsultas.getDthrAvisada()) ||
				oldMamInterconsultas.getParecerConsultor() != newMamInterconsultas.getParecerConsultor() ||(newMamInterconsultas.getDthrConhecimentoConsultor() != null && newMamInterconsultas.getDthrConhecimentoConsultor() != oldMamInterconsultas.getDthrConhecimentoConsultor()) ||
				(newMamInterconsultas.getDthrVisualizaResposta() != null && newMamInterconsultas.getDthrVisualizaResposta() != oldMamInterconsultas.getDthrVisualizaResposta()) ||
				oldMamInterconsultas.getConsulta().getNumero() != newMamInterconsultas.getConsulta().getNumero() || (newMamInterconsultas.getDthrMarcacaoRetorno() != null && newMamInterconsultas.getDthrMarcacaoRetorno() != oldMamInterconsultas.getDthrMarcacaoRetorno()) ||
				(newMamInterconsultas.getDthrAvisaRetorno() != null && newMamInterconsultas.getDthrAvisaRetorno() != oldMamInterconsultas.getDthrAvisaRetorno())){
			return true;
		} else {
			return false;
		}
	}
	/**
	 * ORADB Trigger MAMP_ENFORCE_IEO_RULES <br/>
	 * 
	 * @author marcelo.deus
	 * @throws ApplicationBusinessException 
	 */
	public void executarEnforcerInterconsultas(MamInterconsultas oldMamInterconsultas, MamInterconsultas interconsultas, DominioOperacaoBanco operacao) throws ApplicationBusinessException{
		operacaoInsert(interconsultas, operacao);
		operacaoUpdate(oldMamInterconsultas, interconsultas, operacao);
	}
	//Operação de Insert no banco.
	private void operacaoInsert(MamInterconsultas interconsultas,DominioOperacaoBanco operacao) throws ApplicationBusinessException {
		if(operacao.equals(DominioOperacaoBanco.INS)){
			if(interconsultas.getMamInterconsultas().getSeq() != null){
				if(getMamInterconsultasDAO().verificarExistenciaInterconsultaPorNumeroEspecialidadeSequencia(interconsultas.getConsulta().getNumero(),interconsultas.getEspecialidade().getSeq(),interconsultas.getSeq())){
					throw new ApplicationBusinessException(GestaoInterconsultasRNExceptionCode.MAM_01635, Severity.INFO);
				}
				if(getMamInterconsultasDAO().verificaExistenciaInterconsultaPorPacienteEspecialidadeNumeroValido(interconsultas.getPaciente().getCodigo(),interconsultas.getEspecialidade().getSeq(),interconsultas.getSeq(), null)){
					throw new ApplicationBusinessException(GestaoInterconsultasRNExceptionCode.MAM_01666, Severity.INFO);
				}
				if(getMamInterconsultasDAO().verificaExistenciaInterconsultaPorNumeroEspecialidadeAdmSeq(interconsultas.getConsulta().getNumero(), interconsultas.getEspecialidadeAdm().getSeq(),interconsultas.getSeq())){
					throw new ApplicationBusinessException(GestaoInterconsultasRNExceptionCode.MAM_01635, Severity.INFO);
				}
				if(getMamInterconsultasDAO().verificaExistenciaInterconsultaPorPacienteEspecialidadeNumeroValido(interconsultas.getPaciente().getCodigo(), interconsultas.getEspecialidadeAdm().getSeq(), interconsultas.getSeq(),null)){
					throw new ApplicationBusinessException(GestaoInterconsultasRNExceptionCode.MAM_01666, Severity.INFO);
				}
			}
		}
	}
	//Operação de Update no banco.	
	private void operacaoUpdate(MamInterconsultas oldMamInterconsultas,MamInterconsultas interconsultas, DominioOperacaoBanco operacao)throws ApplicationBusinessException {
		if(operacao.equals(DominioOperacaoBanco.UPD)){
			if(!oldMamInterconsultas.getEspecialidade().getSeq().equals(interconsultas.getEspecialidade().getSeq()) || !oldMamInterconsultas.getConsulta().getNumero().equals(interconsultas.getConsulta().getNumero())){
				if(getMamInterconsultasDAO().verificarExistenciaInterconsultaPorNumeroEspecialidadeSequencia(interconsultas.getConsulta().getNumero(), interconsultas.getEspecialidade().getSeq(), interconsultas.getSeq())){
					throw new ApplicationBusinessException(GestaoInterconsultasRNExceptionCode.MAM_01635, Severity.INFO);
				}
				if(getMamInterconsultasDAO().verificaExistenciaInterconsultaPorPacienteEspecialidadeNumeroValido(interconsultas.getPaciente().getCodigo(), interconsultas.getEspecialidade().getSeq(), interconsultas.getSeq(),null)){
					throw new ApplicationBusinessException(GestaoInterconsultasRNExceptionCode.MAM_01666, Severity.INFO);
				}
			}
			if(oldMamInterconsultas.getEspecialidadeAdm().getSeq() != interconsultas.getEspecialidadeAdm().getSeq() ||oldMamInterconsultas.getConsulta().getNumero() != interconsultas.getConsulta().getNumero()){
				if(getMamInterconsultasDAO().verificaExistenciaInterconsultaPorNumeroEspecialidadeAdmSeq(interconsultas.getConsulta().getNumero(), interconsultas.getEspecialidadeAdm().getSeq(), interconsultas.getSeq())){
					throw new ApplicationBusinessException(GestaoInterconsultasRNExceptionCode.MAM_01635, Severity.INFO);
				}
				if(getMamInterconsultasDAO().verificaExistenciaInterconsultaPorPacienteEspecialidadeNumeroValido(interconsultas.getPaciente().getCodigo(), interconsultas.getEspecialidadeAdm().getSeq(), interconsultas.getSeq(),null)){
					throw new ApplicationBusinessException(GestaoInterconsultasRNExceptionCode.MAM_01666, Severity.INFO);
				}
			}
		}
	}

	public void atualizarInterconsulta(MamInterconsultas parametroSelecionado) {
		mamInterconsultasDAO.merge(parametroSelecionado);
	}
		
	public Date obterDtPrevisaoInterconsulta(Short espSeq, Short caaSeq) throws ApplicationBusinessException{
		Date dataPrevisao = null; Long vQtdePacPendente = null;Integer vConta = 0; 
		AacConsultas consulta = new AacConsultas();
		consulta.setCaaSeq(Short.valueOf(this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_CAA_INTERCONSULTA).getVlrNumerico().toString()));
		
		vQtdePacPendente = this.mamInterconsultasDAO.obterQtdInterconsulta(espSeq);
		List<AacConsultas> cConsulta = this.aacConsultasDAO.obterDataPrevisaoInterconsulta(espSeq, consulta.getCaaSeq());
		if(!cConsulta.isEmpty()){
			for(AacConsultas rConsulta : cConsulta){
				vConta = vConta + 1;
				if(vConta > vQtdePacPendente){
					dataPrevisao = rConsulta.getDtConsulta();
	}
}
		}
		return dataPrevisao;
	}
	
	public void inserirSolicitacaoInterconsulta(List<SolicitaInterconsultaVO> solicitaInterconsultaVO, Integer numConsulta) throws ParseException, ApplicationBusinessException {
		
		DateFormat formatter = new SimpleDateFormat("dd/MM/yy");  
		Date dthrCriacao = (Date)formatter.parse(DateUtil.obterDataFormatada(new Date(), "dd/MM/yy"));
		if(!solicitaInterconsultaVO.isEmpty()){
			for (SolicitaInterconsultaVO interconsulta : solicitaInterconsultaVO) {
				MamInterconsultas mamInterconsultas =  new MamInterconsultas(); 
				AghEspecialidades especialidade  = aghEspecialidadesDAO.obterPorChavePrimaria(interconsulta.getEspSeq());
				AipPacientes paciente = aipPacientesDAO.obterPorChavePrimaria(interconsulta.getCodPaciente());
				AacConsultas consulta= aacConsultasDAO.obterPorChavePrimaria(numConsulta);
				RapServidores servidorLogado = registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado(), new Date());

				if(interconsulta.getEquipeSeq() != null){
				AghEquipes equipe  = aghEquipesDAO.obterPorChavePrimaria(interconsulta.getEquipeSeq());
					mamInterconsultas.setEquipe(equipe);
				}
				if(interconsulta.getRpfMatricula() != null && interconsulta.getRpfVinCodigo() != null){
				RapServidoresId id = new RapServidoresId(interconsulta.getRpfMatricula(), interconsulta.getRpfVinCodigo());
				RapServidores servidor =  rapServidoresDAO.obterPorChavePrimaria(id);
					mamInterconsultas.setServidorResponsavel(servidor);
				}
						
				mamInterconsultas.setServidor(servidorLogado);
				mamInterconsultas.setServidorValida(servidorLogado);
				mamInterconsultas.setEspecialidade(especialidade);
				mamInterconsultas.setEspecialidadeAdm(especialidade);
				mamInterconsultas.setPaciente(paciente);
				mamInterconsultas.setSituacao(DominioSituacaoInterconsultasPesquisa.P);
				mamInterconsultas.setIndImpresso("N");
				mamInterconsultas.setPendente(DominioIndPendenteAmbulatorio.V);
				mamInterconsultas.setPrioridadeAprovada(DominioPrioridadeInterconsultas.B);
				mamInterconsultas.setPrioridade("B");
				mamInterconsultas.setAvaliacao(DominioAvaliacaoInterconsulta.L);
				mamInterconsultas.setIndUrgente("N");
				mamInterconsultas.setDigitado(true);
				mamInterconsultas.setDthrCriacao(dthrCriacao);
				mamInterconsultas.setDthrValida(dthrCriacao);
				mamInterconsultas.setConsulta(consulta);
				
					mamInterconsultas.setObservacao(interconsulta.getObservacao());
				mamInterconsultasDAO.persistir(mamInterconsultas);
			
			}
		}
	}
	
	/**
	 * @ORADB:RN_IEOP_VER_CONSULTA
	 */
	public Boolean verificarInterconsulta(SolicitaInterconsultaVO solicitaInterconsultaVO) throws ApplicationBusinessException{
		Boolean interconsulta = false;
		if(solicitaInterconsultaVO != null){
			List<AacConsultas> result = this.aacConsultasDAO.obterInterconsulta(solicitaInterconsultaVO.getEspSeq(), solicitaInterconsultaVO.getCodPaciente());
				if(!result.isEmpty()){
					interconsulta =  true;
			}
		}
		return interconsulta;
	}
	
	public Boolean verificarInterconsultaAux(SolicitaInterconsultaVO solicitaInterconsultaVO) throws ApplicationBusinessException{
		Boolean interconsulta = false;
		if(solicitaInterconsultaVO != null){
			List<MamInterconsultas> result = this.mamInterconsultasDAO.obterInterconsulta(solicitaInterconsultaVO.getEspSeq(), solicitaInterconsultaVO.getCodPaciente());
				if(!result.isEmpty()){
					interconsulta =  true;
}
		}
		return interconsulta;
	}
}
