package br.gov.mec.aghu.controleinfeccao.business;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.configuracao.dao.AghAtendimentoDAO;
import br.gov.mec.aghu.configuracao.dao.AghUnidadesFuncionaisDAO;
import br.gov.mec.aghu.controleinfeccao.LocalNotificacaoOrigemRetornoVO;
import br.gov.mec.aghu.controleinfeccao.dao.MciEtiologiaInfeccaoDAO;
import br.gov.mec.aghu.controleinfeccao.dao.MciMvtoInfeccaoTopografiasDAO;
import br.gov.mec.aghu.controleinfeccao.dao.MciMvtoMedidaPreventivasDAO;
import br.gov.mec.aghu.controleinfeccao.dao.MciPatologiaInfeccaoDAO;
import br.gov.mec.aghu.controleinfeccao.dao.MciSolicLeitoIsolamentoDAO;
import br.gov.mec.aghu.controleinfeccao.dao.MciTopografiaProcedimentoDAO;
import br.gov.mec.aghu.controleinfeccao.vo.DoencaInfeccaoVO;
import br.gov.mec.aghu.controleinfeccao.vo.NotificacaoMedidasPreventivasVO;
import br.gov.mec.aghu.controleinfeccao.vo.OrigemInfeccoesVO;
import br.gov.mec.aghu.dominio.DominioTipoMovimentoLocalNotificacao;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.internacao.dao.AinLeitosDAO;
import br.gov.mec.aghu.internacao.dao.AinQuartosDAO;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AinQuartos;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MciEtiologiaInfeccao;
import br.gov.mec.aghu.model.MciMvtoInfeccaoTopografias;
import br.gov.mec.aghu.model.MciMvtoMedidaPreventivas;
import br.gov.mec.aghu.model.MciPatologiaInfeccao;
import br.gov.mec.aghu.model.MciSolicLeitoIsolamento;
import br.gov.mec.aghu.model.MciTopografiaProcedimento;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.paciente.dao.AipPacientesDAO;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.registrocolaborador.dao.RapServidoresDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;


@Stateless
public class NotificacaoMedidasPreventivasRN extends BaseBusiness {

	
	private static final String _HIFEN_ = " - ";

	/**
	 * 
	 */
	private static final long serialVersionUID = 8602619606026658938L;

	private static final Log LOG = LogFactory.getLog(NotificacaoMedidasPreventivasRN.class);
	
	@Inject
	private MciMvtoMedidaPreventivasDAO mciMvtoMedidaPreventivasDAO;
	
	@Inject
	private MciMvtoInfeccaoTopografiasDAO mciMvtoInfeccaoTopografiasDAO;
	
	@Inject
	private MciSolicLeitoIsolamentoDAO mciSolicLeitoIsolamentoDAO;
	
	@Inject
	private AinLeitosDAO ainLeitosDAO;
	
	@Inject
	private AghUnidadesFuncionaisDAO aghUnidadesFuncionaisDAO;
	
	@Inject
	private MciPatologiaInfeccaoDAO mciPatologiaInfeccaoDAO;
	
	@Inject
	private AghAtendimentoDAO aghAtendimentoDAO;
	
	@Inject
	private MciEtiologiaInfeccaoDAO etiologiaInfeccaoDAO;
	
	@Inject
	private MciTopografiaProcedimentoDAO mciTopografiaProcedimentoDAO;

	@Inject
	private RapServidoresDAO rapServidoresDAO;
	
	@Inject
	private AinQuartosDAO ainQuartosDAO;
	
	@Inject
	private AipPacientesDAO aipPacientesDAO;

	@EJB 
	private NotificacoesRN notificacoesRN;
	
	@EJB
	private MciMvtoMedidaPreventivaRN mciMvtoMedidaPreventivaRN;
	
	@EJB
	private MciMvtoInfeccaoTopografiaRN mciMvtoInfeccaoTopografiaRN;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IInternacaoFacade internacaoFacade;
		
	@Override
	public Log getLogger() {
		return LOG;
	}
	
	private enum NotificacaoMedidasPreventivasRNException implements BusinessExceptionCode {
		P_AGHU_CCIH_NRO_DIAS_ATRAS_NOTIFICACAO, MENSAGEM_DATA_INSTALACAO_NMP, MENSAGEM_RESTRICAO_GRAVACAO_NMP, MENSAGEM_RESTRICAO_EXCLUSAO_NMP,  MENSAGEM_NOTIFICACAO_ABERTA_NMP, MENSAGEM_NOTIFICACAO_TOPOGRAFIA_ABERTA_NMP,
		MENSAGEM_DIALISE_NMP, MENSAGEM_VALIDACAO_ENCERRAMENTO_NMP, MENSAGEM_DATA_ENCERRAMENTO_ATUAL_NMP, MENSAGEM_PERIODO_NMP, MENSAGEM_ETIOLOGIA_NMP, MENSAGEM_ETIOLOGIA_ASSOCIADA_TOPOGRAFIA_NMP, REGISTRO_NAO_EXISTE_NMP;

	}	

	// U1 - Atualiza Notificação de medida preventiva
	private void atualizarMciMvtoNotificacaoMedidaPreventiva(NotificacaoMedidasPreventivasVO vo, MciMvtoInfeccaoTopografias topografia) throws BaseException {
		MciMvtoMedidaPreventivas entity = mciMvtoMedidaPreventivasDAO.obterOriginal(vo.getSeq());
		if(entity == null || entity.getSeq() == null) {
			throw new ApplicationBusinessException(NotificacaoMedidasPreventivasRNException.REGISTRO_NAO_EXISTE_NMP);
		}
		entity.setMciMvtoInfeccaoTopografias(topografia);
		salvarDadosBasicos(vo, entity);
		salvarAlteracaoAtendimento(vo, entity);
		salvarAlteracaoCCI(vo, entity);
		salvarDadosEncerramento(vo, entity);
		inserirMciMvtoMedidaPreventivasJn(entity, DominioOperacoesJournal.UPD);
		mciMvtoMedidaPreventivasDAO.merge(entity);
	}


	private void salvarDadosBasicos(NotificacaoMedidasPreventivasVO vo, MciMvtoMedidaPreventivas entity) {
		entity.setDataInicio(vo.getDtInicio());		
		entity.setMciEtiologiaInfeccao(obterEtiologia(vo.getEinTipo()));		
		entity.setPatologiaInfeccao(obterPatologiaInfeccao(vo.getPaiSeq()));
	}
	
	private void salvarDadosEncerramento(NotificacaoMedidasPreventivasVO vo, MciMvtoMedidaPreventivas entity) {
		if (vo.getDtFim() != null && vo.getMotivoEncerramento() != null) {
			entity.setDataFim(vo.getDtFim());
			entity.setMotivoEncerramento(vo.getMotivoEncerramento());
			entity.setServidorEncerrado(obterRapServidores(vo.getSerMatriculaEncerrado(), vo.getSerVinCodigoEncerrado()));		
		}
	}

	private void salvarAlteracaoCCI(NotificacaoMedidasPreventivasVO vo,	MciMvtoMedidaPreventivas entity) {
		if(entity.getConfirmacaoCci().getCodigo() != vo.getIndConfirmacaoCCI().getCodigo()) {
			entity.setServidorEncerrado(obterRapServidores(vo.getSerMatriculaConfirmado(), vo.getSerVinCodigoConfirmado()));	
			entity.setConfirmacaoCci(vo.getIndConfirmacaoCCI());			
		}
	}

	private void salvarAlteracaoAtendimento(NotificacaoMedidasPreventivasVO vo,
			MciMvtoMedidaPreventivas entity) throws ApplicationBusinessException {
		if(entity.getAtendimento() != null && vo.getAtdSeq() != null) {
			if(entity.getAtendimento().getSeq().intValue() != vo.getAtdSeq().intValue()) {				
				AghAtendimentos atendimento = aghAtendimentoDAO.obterPorChavePrimaria(vo.getAtdSeq());
				entity.setAtendimento(atendimento);
				setLocalAtendimento(vo, entity);
			}
		}
	}	

	//U2
	private MciMvtoInfeccaoTopografias atualizarNotificacaoTopografiaAssociada(NotificacaoMedidasPreventivasVO vo) throws BaseException {
		MciMvtoInfeccaoTopografias entity = mciMvtoInfeccaoTopografiasDAO.obterPorChavePrimaria(vo.getMitSeq());
		if(entity == null || entity.getSeq() == null) {
			throw new ApplicationBusinessException(NotificacaoMedidasPreventivasRNException.REGISTRO_NAO_EXISTE_NMP);
		}
		salvarDadosBasicosTopografia(vo, entity);
		salvarAlteracaoAtendimentoTopografia(vo, entity);		
		salvarConfirmacaoCCITopografia(vo, entity);
		salvarDadosEncerramentoTopografia(vo, entity);
		inserirMciMvtoInfeccaoTopografiaJn(entity, DominioOperacoesJournal.UPD);
		mciMvtoInfeccaoTopografiasDAO.merge(entity);
		return entity;
	}

	private void salvarDadosEncerramentoTopografia(NotificacaoMedidasPreventivasVO vo, MciMvtoInfeccaoTopografias entity) {
		if (vo.getDtFim() != null && vo.getMotivoEncerramento() != null) {
			entity.setDataFim(vo.getDtFim());
			entity.setMotivoEncerramento(vo.getMotivoEncerramento());			
			entity.setServidorEncerrado(obterRapServidores(vo.getSerMatriculaEncerrado(), vo.getSerVinCodigoEncerrado()));			
		}
	}
	
	private void salvarConfirmacaoCCITopografia( NotificacaoMedidasPreventivasVO vo, MciMvtoInfeccaoTopografias entity) {
		if(entity.getConfirmacaoCci().getCodigo() != vo.getIndConfirmacaoCCI().getCodigo()) {
			entity.setServidorConfirmado(obterRapServidores(vo.getSerMatriculaConfirmado(),  vo.getSerVinCodigoConfirmado()));
			entity.setConfirmacaoCci(vo.getIndConfirmacaoCCI());				
		}
	}

	private void salvarAlteracaoAtendimentoTopografia(NotificacaoMedidasPreventivasVO vo, MciMvtoInfeccaoTopografias entity) throws BaseException {
		if(entity.getAtendimento() != null && vo.getAtdSeq() != null) {
			if(entity.getAtendimento().getSeq().intValue() != vo.getAtdSeq().intValue()) {				
				AghAtendimentos atendimento = aghAtendimentoDAO.obterPorChavePrimaria(vo.getAtdSeq());
				entity.setAtendimento(atendimento);				
				setLocalAtendimento(vo, entity);
			}				
		}
	}

	private void salvarDadosBasicosTopografia(NotificacaoMedidasPreventivasVO vo, MciMvtoInfeccaoTopografias entity) {
		entity.setDataInicio(vo.getDtInicio());
		entity.setEtiologiaInfeccao(obterEtiologia(vo.getEinTipo()));
		entity.setTopografiaProcedimento(obterTopografiaProcedimento(vo.getTopSeq()));	
	}	
	
	/**
	 * FUNCTION MCIC_VER_DT_INI_ATD
	 * #39814 
	 * @param item
	 * TODO validar isso com Analista, pois talvez seja necessário também validar a data mais recente, caso seja possível
	 * o mesmo paciente passar pelo mesmo tipo de atendimento mais de uma vez.
	 * Devolve a lista ordenada por data atendimento.
	 * @throws ApplicationBusinessException 
	 */
	public List<NotificacaoMedidasPreventivasVO> buscarNotificacoesMedidasPreventivasPorCodigoPaciente(Integer codigoPaciente) throws ApplicationBusinessException {
		List<NotificacaoMedidasPreventivasVO> notificacoes = mciMvtoMedidaPreventivasDAO.buscarNotificacoesMedidasPreventivasPorCodigoPaciente(codigoPaciente);
		for (NotificacaoMedidasPreventivasVO item : notificacoes) {
			setDescricaoNotificacao(item);
			setDescricaoSeqPatologia(item);
			setDescricaoSeqTopografia(item);
			formatarLocalContabilizada(item);
		}
		 CoreUtil.ordenarLista(notificacoes, NotificacaoMedidasPreventivasVO.Fields.DT_ATENDIMENTO.toString(), false);
		 return notificacoes;
	}
	
	public void formatarLocalContabilizada(NotificacaoMedidasPreventivasVO vo) throws ApplicationBusinessException{
		
		AinLeitos leito = null;
		AinQuartos quarto = null;
		AghUnidadesFuncionais unidadeFuncional = null;
		if (vo.getLtoId()!= null) {
			leito = internacaoFacade.obterAinLeitosPorChavePrimaria(vo.getLtoId());
		}
		if (vo.getQrtNumero() != null) {
			quarto = internacaoFacade.obterAinQuartosPorChavePrimaria(vo.getQrtNumero());
		}
		if (vo.getUnfSeq() != null) {
			unidadeFuncional = aghuFacade.obterAghUnidadesFuncionaisPorChavePrimaria(vo.getUnfSeq());
		}
		vo.setLocalizacao(prescricaoMedicaFacade.buscarResumoLocalPaciente2(leito, quarto, unidadeFuncional));
	}
	
	
	public void setDescricaoSeqPatologia(NotificacaoMedidasPreventivasVO item) {
		StringBuilder descricao = new StringBuilder(item.getPaiSeq() == null? "": item.getPaiSeq().toString());
		
		if(StringUtils.isNotBlank(item.getDescricaoPatologia())) {
			descricao.append(_HIFEN_);
			descricao.append(item.getDescricaoPatologia());	
		}
		 item.setDescricaoSeqPatologia(descricao.toString());
	}
	
	public void setDescricaoSeqTopografia(NotificacaoMedidasPreventivasVO item) {
		StringBuilder descricao = new StringBuilder(item.getTopSeq() == null? "": item.getTopSeq().toString());
		if(StringUtils.isNotBlank(item.getTopDescricao())) {
			descricao.append(_HIFEN_);
			descricao.append(item.getTopDescricao());	
		}
		 item.setDescricaoSeqTopografia(descricao.toString());
	}
	
	public void setDescricaoNotificacao(NotificacaoMedidasPreventivasVO item) {
		StringBuilder descricao = new StringBuilder(item.getPaiSeq() == null? "": item.getAtdSeq().toString());
		String atdFormatado = getDtFormatada( item.getDtAtendimento());
		if(StringUtils.isNotBlank(atdFormatado)){
			descricao.append(_HIFEN_);
			descricao.append(atdFormatado);
		}
		if(StringUtils.isNotBlank( item.getDescricaoPatologia())){
			descricao.append(_HIFEN_);
			descricao.append( item.getDescricaoPatologia());
		}
		String dtInicioFormatado = getDtFormatada( item.getDtInicio());
		if(StringUtils.isNotBlank(dtInicioFormatado)){
			descricao.append(_HIFEN_);
			descricao.append(dtInicioFormatado);
		}
		item.setDescricao(descricao.toString());
	}
	
	private String getDtFormatada(Date data){
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		if (data == null) {
			return "";
		} else {
			return sdf.format(data);
		}
	}
	
	/**
	 * #1297 - C2 - Método auxiliar que monta o nome que será visualizado na
	 * SuggestionBox de Doença/Infecção.
	 * @param param
	 * @return
	 */
	public List<DoencaInfeccaoVO> buscarDoencaInfeccao(String param) {
		List<DoencaInfeccaoVO> lista = mciPatologiaInfeccaoDAO.buscarDoencaInfeccao(param);
		for (DoencaInfeccaoVO item : lista) {
			if(StringUtils.isNotBlank(item.getDescPatologia()) && StringUtils.isNotBlank(item.getDescPalavraChave())) {
				item.setPalavraChavePatologia(item.getDescPalavraChave().concat(_HIFEN_).concat(item.getDescPatologia()));
			} else if (StringUtils.isNotBlank(item.getDescPatologia())) {
				item.setPalavraChavePatologia(item.getDescPatologia());
			} else {
				item.setPalavraChavePatologia(item.getDescPalavraChave());
			}
		}
		CoreUtil.ordenarLista(lista, DoencaInfeccaoVO.Fields.PALAVRA_CHAVE_PATOLOGIA.toString(), true);
		return lista;
	}
	
	public Long buscarDoencaInfeccaoCount(String param) {
		return mciPatologiaInfeccaoDAO.buscarDoencaInfeccaoCount(param);
	}
	
	public List<OrigemInfeccoesVO> buscarOrigemInfeccao(String param) {
		List<OrigemInfeccoesVO> lista = etiologiaInfeccaoDAO.suggestionBoxTopografiaOrigemInfeccoes(param);
		for (OrigemInfeccoesVO item : lista) {
			if(StringUtils.isNotBlank(item.getDescricao()) && StringUtils.isNotBlank(item.getTextoNotificacao())) {
				item.setDescricao(item.getDescricao().concat(_HIFEN_).concat(item.getTextoNotificacao()));
			} else if (StringUtils.isNotBlank(item.getTextoNotificacao()) && StringUtils.isBlank(item.getDescricao())) {
				item.setDescricao(item.getTextoNotificacao());
			}
		}
		return lista;
	}
	
	public Long buscarOrigemInfeccaoCount(String param) {
		return etiologiaInfeccaoDAO.suggestionBoxTopografiaOrigemInfeccoesCount(param);
	}
	
	//I1
	private void inserirMciMvtoMedidaPreventivasJn(MciMvtoMedidaPreventivas entity, DominioOperacoesJournal operacao) throws BaseException {
		mciMvtoMedidaPreventivaRN.createJournal(entity, operacao);
	}
	
	//I2
	private void inserirMciMvtoInfeccaoTopografiaJn(MciMvtoInfeccaoTopografias entity, DominioOperacoesJournal operacao) throws BaseException {
		mciMvtoInfeccaoTopografiaRN.createJournal(entity, operacao);
	}	
	
	//I3 
	private void inserirMciMedidaPreventiva(NotificacaoMedidasPreventivasVO vo, MciMvtoInfeccaoTopografias topografia) throws BaseException {
		MciMvtoMedidaPreventivas entity = new MciMvtoMedidaPreventivas();	
		entity.setPaciente(obterAinPaciente(vo.getPacCodigo()));
		entity.setAtendimento(obterAtendimento(vo.getAtdSeq()));
		setLocalAtendimento(vo, entity);
		entity.setDataInicio(vo.getDtInicio());
		entity.setDataFim(vo.getDtFim());
		entity.setMotivoEncerramento(vo.getMotivoEncerramento());
		entity.setMciEtiologiaInfeccao(obterEtiologia(vo.getEinTipo()));
		entity.setPatologiaInfeccao(obterPatologiaInfeccao(vo.getPaiSeq()));
		entity.setMciMvtoInfeccaoTopografias(topografia);
		entity.setConfirmacaoCci(vo.getIndConfirmacaoCCI());
		entity.setimpressao(true);
		entity.setIsolamento(vo.getUsoQuartoPrivativo());
		entity.setCriadoEm(new Date());
		setRapServidores(vo, entity);
		mciMvtoMedidaPreventivasDAO.persistir(entity);
	}

	private void setRapServidores(NotificacaoMedidasPreventivasVO vo, MciMvtoMedidaPreventivas entity) {
		entity.setServidor(obterRapServidores(vo.getSerMatricula(), vo.getSerVinCodigo()));
		entity.setServidorConfirmado(obterRapServidores(vo.getSerMatriculaConfirmado(), vo.getSerVinCodigoConfirmado()));
		entity.setServidorEncerrado(obterRapServidores(vo.getSerMatriculaEncerrado(), vo.getSerVinCodigoEncerrado()));
	}
	
	private void setLocalAtendimento(NotificacaoMedidasPreventivasVO vo, MciMvtoMedidaPreventivas entity)
			throws ApplicationBusinessException {
		LocalNotificacaoOrigemRetornoVO local = notificacoesRN.obterLocalAtendimento(vo.getAtdSeq(), vo.getEinTipo(), DominioTipoMovimentoLocalNotificacao.MIT, vo.getDtInicio(), null);
		entity.setQuarto(obterAinQuarto(local.getQrtNumero()));
		entity.setLeito(obterAinLeito(local.getLtoLtoId()));
		entity.setUnidadeFuncional(obterAghUnidadesFuncionais(local.getUnfSeq()));
		entity.setQuartoNotificado(obterAinQuarto(local.getQrtNumeroNotificado()));
		entity.setLeitoNotificado(obterAinLeito(local.getLtoLtoIdNotificado()));
		entity.setUnidadeFuncionalNotificada(obterAghUnidadesFuncionais(local.getUnfSeqNotificado()));		
	}	
	
	// I4
	private MciMvtoInfeccaoTopografias inserirMciMvtoInfeccaoTopografia(NotificacaoMedidasPreventivasVO vo) throws BaseException {
		MciMvtoInfeccaoTopografias entity = new MciMvtoInfeccaoTopografias();
		if(vo.getPacCodigo() != null){
			entity.setPaciente((aipPacientesDAO.obterPorChavePrimaria(vo.getPacCodigo())));
		}
		if(vo.getAtdSeq() != null) {
			entity.setAtendimento(aghAtendimentoDAO.obterPorChavePrimaria(vo.getAtdSeq()));
		}
		// Chamada para #39835
		setLocalAtendimento(vo, entity);
		entity.setDataInicio(vo.getDtInicio());
		entity.setDataFim(vo.getDtFim());
		entity.setMotivoEncerramento(vo.getMotivoEncerramento());
		entity.setEtiologiaInfeccao(obterEtiologia(vo.getEinTipo()));
		entity.setConfirmacaoCci(vo.getIndConfirmacaoCCI());
		entity.setTopografiaProcedimento(obterTopografiaProcedimento(vo.getTopSeq()));
		entity.setCriadoEm(new Date());
		setRapServidores(vo, entity);
		mciMvtoInfeccaoTopografiasDAO.persistir(entity);
		return entity;
	}

	private void setRapServidores(NotificacaoMedidasPreventivasVO vo,
			MciMvtoInfeccaoTopografias entity) {		
		entity.setServidor(obterRapServidores(vo.getSerMatricula(), vo.getSerVinCodigo()));
		entity.setServidorConfirmado(obterRapServidores(vo.getSerMatriculaConfirmado(), vo.getSerVinCodigoConfirmado()));
		entity.setServidorEncerrado(obterRapServidores(vo.getSerMatriculaEncerrado(), vo.getSerVinCodigoEncerrado()));	
	}

	private void setLocalAtendimento(NotificacaoMedidasPreventivasVO vo, MciMvtoInfeccaoTopografias entity)
			throws ApplicationBusinessException {
		LocalNotificacaoOrigemRetornoVO local = notificacoesRN.obterLocalAtendimento(vo.getAtdSeq(), vo.getEinTipo(), DominioTipoMovimentoLocalNotificacao.MIT, vo.getDtInicio(), null);
		entity.setQuarto(obterAinQuarto(local.getQrtNumero()));
		entity.setLeito(obterAinLeito(local.getLtoLtoId()));
		entity.setUnidadeFuncional(obterAghUnidadesFuncionais(local.getUnfSeq()));
		entity.setQuartoNotificado(obterAinQuarto(local.getQrtNumeroNotificado()));
		entity.setLeitoNotificado(obterAinLeito(local.getLtoLtoIdNotificado()));
		entity.setUnidadeFuncionalNotificada(obterAghUnidadesFuncionais(local.getUnfSeqNotificado()));		
	}	

	//RN1
	public void deletarNotificacaoMedidaPreventiva(NotificacaoMedidasPreventivasVO vo) throws BaseException {
		//[RN4]
		notificacoesRN.validarNumeroDiasDecorridosCriacaoRegistro(vo.getCriadoEm(), NotificacaoMedidasPreventivasRNException.MENSAGEM_PERIODO_NMP);
		if(vo.getSeq() != null) {
		List<MciSolicLeitoIsolamento> isolamentos = mciSolicLeitoIsolamentoDAO.pesquisarMciSolicLeitoIsolamentoPorSeqNotificacao(vo.getSeq());
		if (isolamentos != null && !isolamentos.isEmpty()) {
			String itens = buscarDescricaoItensAssociados(isolamentos);
			throw new ApplicationBusinessException(NotificacaoMedidasPreventivasRNException.MENSAGEM_RESTRICAO_EXCLUSAO_NMP, itens);
		}
		}
		if (vo.getTopSeq() != null) {
			deletarTopografiaAssociada(vo);
		}
		deletarMedidaPreventiva(vo);
	}

	private void deletarMedidaPreventiva(NotificacaoMedidasPreventivasVO vo)
			throws BaseException {
		MciMvtoMedidaPreventivas notificacao = mciMvtoMedidaPreventivasDAO.obterPorChavePrimaria(vo.getSeq());
		if(notificacao == null || notificacao.getSeq() == null) {
			throw new ApplicationBusinessException(NotificacaoMedidasPreventivasRNException.REGISTRO_NAO_EXISTE_NMP);
		}
		inserirMciMvtoMedidaPreventivasJn(notificacao, DominioOperacoesJournal.DEL);
		mciMvtoMedidaPreventivasDAO.remover(notificacao);
	}
	
	private void deletarTopografiaAssociada(NotificacaoMedidasPreventivasVO vo)
			throws BaseException {
		MciMvtoInfeccaoTopografias topografia = mciMvtoInfeccaoTopografiasDAO.obterPorChavePrimaria(vo.getMitSeq());
		if(topografia == null || topografia.getSeq() == null) {
			throw new ApplicationBusinessException(NotificacaoMedidasPreventivasRNException.REGISTRO_NAO_EXISTE_NMP);
		}
		inserirMciMvtoInfeccaoTopografiaJn(topografia, DominioOperacoesJournal.DEL);
		mciMvtoInfeccaoTopografiasDAO.remover(topografia);
	}	

	//RN2
	public void atualizarNotificacaoMedidaPreventiva(NotificacaoMedidasPreventivasVO vo) throws BaseException, BaseListException {
		MciMvtoInfeccaoTopografias topografia = null;
		// se as duas seqs diferentes de null, então atualiza a topo
		if(vo.getTopSeq() != null && vo.getMitSeq() != null) {
			topografia = atualizarNotificacaoTopografiaAssociada(vo);
		// se só o Top foi informado, quer dizer que é uma criação	
		} else if (vo.getTopSeq() != null && vo.getMitSeq() == null) {
			topografia =  inserirMciMvtoInfeccaoTopografia(vo);
		}
		atualizarMciMvtoNotificacaoMedidaPreventiva(vo, topografia);
	}	

	//RN3
	public void inserirNotificacaoMedidaPreventiva(NotificacaoMedidasPreventivasVO vo) throws BaseException, BaseListException {
		MciMvtoInfeccaoTopografias topografia = null;
		if(vo.getTopSeq()!= null) {
			topografia = inserirMciMvtoInfeccaoTopografia(vo);			
		}
		inserirMciMedidaPreventiva(vo, topografia);
	}
	
	//RN5
	public void validarCadastroEdicaoNotificacao(NotificacaoMedidasPreventivasVO vo) throws BaseListException {
		BaseListException excecaoList = new BaseListException(NotificacaoMedidasPreventivasRNException.MENSAGEM_RESTRICAO_GRAVACAO_NMP);
		Boolean isDataInstalacaoInvalida = validarDataDeInstalacao(vo, excecaoList);
		Boolean existeDuplicidade = validarDuplicacaoNotificacao(vo, excecaoList);
		Boolean existeTopografiaNaoEncerrada = validarTopografiaNaoEncerrada(vo, excecaoList);
		Boolean isEtiologiaInvalida = validarEtiologia(vo, excecaoList);
		Boolean isEncerramentoInvalido = validarEncerramento(vo, excecaoList);
		Boolean isDataEncerramentoInvalido = validarDataEncerramento(vo, excecaoList);
		if (existeDuplicidade || existeTopografiaNaoEncerrada || isEtiologiaInvalida || isEncerramentoInvalido 
		   || isDataInstalacaoInvalida || isDataEncerramentoInvalido) {			
			throw excecaoList;
		}
	}
	
	private Boolean validarDataEncerramento(NotificacaoMedidasPreventivasVO vo,BaseListException excecaoList) throws BaseListException {
		Boolean hasException = false;
		try {
			notificacoesRN.validarDataEncerramento(vo.getDtFim(), vo.getDtInicio(), NotificacaoMedidasPreventivasRNException.MENSAGEM_DATA_ENCERRAMENTO_ATUAL_NMP);
		} catch (ApplicationBusinessException e) {
			excecaoList.add(e);
			hasException = true;
		}
		return hasException;
	}

	private Boolean validarTopografiaNaoEncerrada(NotificacaoMedidasPreventivasVO vo,
			BaseListException excecaoList) throws BaseListException {
		Boolean existe = mciMvtoInfeccaoTopografiasDAO.validarExistenciaTopografiaNaoEncerradaPorAtendimento(vo.getMitSeq() , vo.getAtdSeq());
		Boolean hasException = false;
		if(existe) {
			excecaoList.add(new ApplicationBusinessException(NotificacaoMedidasPreventivasRNException.MENSAGEM_NOTIFICACAO_TOPOGRAFIA_ABERTA_NMP));
			hasException = true;
		}
		return hasException;
	}

	private Boolean validarEncerramento(NotificacaoMedidasPreventivasVO vo,BaseListException excecaoList) throws BaseListException {
		Boolean hasException = false;
			if ((vo.getDtFim() != null || vo.getMotivoEncerramento() != null)) {
				if(vo.getDtFim() == null || vo.getMotivoEncerramento() == null){
					excecaoList.add(new ApplicationBusinessException(NotificacaoMedidasPreventivasRNException.MENSAGEM_VALIDACAO_ENCERRAMENTO_NMP));
					hasException = true;
				}
			}
		return hasException;
	}

	private Boolean validarEtiologia(NotificacaoMedidasPreventivasVO vo,
		BaseListException excecaoList) throws BaseListException{
		Boolean hasException = false;
		if(vo.getTopSeq() != null && StringUtils.isBlank(vo.getEinTipo())) {
			excecaoList.add(new ApplicationBusinessException(NotificacaoMedidasPreventivasRNException.MENSAGEM_ETIOLOGIA_NMP));
			hasException = true;
		} else 	if(vo.getTopSeq() == null && StringUtils.isNotBlank(vo.getEinTipo())) {
			excecaoList.add(new ApplicationBusinessException(NotificacaoMedidasPreventivasRNException.MENSAGEM_ETIOLOGIA_ASSOCIADA_TOPOGRAFIA_NMP));
			hasException = true;
		}
		return hasException;
	}

	private Boolean validarDuplicacaoNotificacao(NotificacaoMedidasPreventivasVO vo, BaseListException excecaoList)  throws BaseListException{
		Boolean hasException = false;
		Boolean existeNotificao = mciMvtoMedidaPreventivasDAO.
		verificarMedidaPreventivaNaoEncerradaPorPatologiaAtendimento(vo.getSeq(),vo.getPaiSeq(), vo.getAtdSeq());
		if(existeNotificao && vo.getDtFim() == null) {
			excecaoList.add(new ApplicationBusinessException(NotificacaoMedidasPreventivasRNException.MENSAGEM_NOTIFICACAO_ABERTA_NMP));
			hasException = true;
		}
		return hasException;
	}
	
	private Boolean validarDataDeInstalacao( NotificacaoMedidasPreventivasVO vo, BaseListException excecaoList) throws BaseListException {
		Boolean hasException = false;			
		Long paramDias;
		try {
			paramDias = obterParametroLimiteDiasCadastroNotificacao();			
			if(DateUtil.validaDataTruncadaMaior(vo.getDtInicio(), new Date()) || obterDiferencaEntreDatas(DateUtil.truncaData(vo.getDtInicio())) > paramDias){
				excecaoList.add(new ApplicationBusinessException(NotificacaoMedidasPreventivasRNException.MENSAGEM_DATA_INSTALACAO_NMP, paramDias));
				hasException = true;
			}
		} catch (BaseException e) {
			excecaoList.add(new ApplicationBusinessException(NotificacaoMedidasPreventivasRNException.MENSAGEM_DATA_INSTALACAO_NMP));
			hasException = true;
		}
		
		return hasException;
	}
	
	private RapServidores obterRapServidores(Integer matricula, Short vinCodigo) {
		if(matricula == null || vinCodigo == null){
			return null;	 
		}
		return rapServidoresDAO.obterPorChavePrimaria(new RapServidoresId(matricula, vinCodigo));
	}
	
	private AipPacientes obterAinPaciente(Integer seq) {
		if(seq == null) {
			return null;
		}
		return aipPacientesDAO.obterPorChavePrimaria(seq);
		
	}
	
	private AinQuartos obterAinQuarto(Short numero) {
		if(numero == null) {
			return null;
		}
		return ainQuartosDAO.obterPorChavePrimaria(numero);
	}
	
	private AinLeitos obterAinLeito(String leitoID) {
		if(StringUtils.isBlank(leitoID)) {
			return null;
		}
		return ainLeitosDAO.obterPorChavePrimaria(leitoID);
	}
	
	private AghAtendimentos obterAtendimento(Integer seq) {
		if(seq == null) {
			return null;
		}
		return aghAtendimentoDAO.obterPorChavePrimaria(seq);
	}
	
	private AghUnidadesFuncionais obterAghUnidadesFuncionais(Short seq) {
		if(seq == null) {
			return null;	
		}
		return aghUnidadesFuncionaisDAO.obterPorChavePrimaria(seq);
	}
	
	private MciEtiologiaInfeccao obterEtiologia(String codigo) {
		if (StringUtils.isBlank(codigo)) {
			return null;
		}
		return etiologiaInfeccaoDAO.obterPorChavePrimaria(codigo);
	}
	
	private MciTopografiaProcedimento obterTopografiaProcedimento(Short seq) {
		if (seq == null) {
			return null;
		}
		return mciTopografiaProcedimentoDAO.obterPorChavePrimaria(seq);
	}
	
	private MciPatologiaInfeccao obterPatologiaInfeccao(Integer seq) {
		if(seq == null) {
			return null;
		}		
		return mciPatologiaInfeccaoDAO.obterPorChavePrimaria(seq);
	}
	
	private String buscarDescricaoItensAssociados(List<MciSolicLeitoIsolamento> itens) {
		StringBuilder descricoes = new StringBuilder();
		for (MciSolicLeitoIsolamento item : itens) {
			AghAtendimentos atd = item.getAghAtendimentos();
			descricoes.append(atd == null ? "" : atd.getSeq());
			if (itens.indexOf(item) < (itens.size() - 1)) {
				descricoes.append(", ");
			}
		}
		return descricoes.toString().concat(".");
	}
	
	private Integer obterDiferencaEntreDatas(Date dataInstalacao){
		return DateUtil.calcularDiasEntreDatas(dataInstalacao, new Date());
	}
	
	private Long obterParametroLimiteDiasCadastroNotificacao() throws BaseListException, BaseException {
		return parametroFacade.buscarValorLong(AghuParametrosEnum.P_AGHU_CCIH_NRO_DIAS_ATRAS_NOTIFICACAO);	
	}

}
