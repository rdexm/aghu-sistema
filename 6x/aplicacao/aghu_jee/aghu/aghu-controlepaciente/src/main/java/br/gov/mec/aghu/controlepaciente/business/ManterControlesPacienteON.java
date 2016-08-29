package br.gov.mec.aghu.controlepaciente.business;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.exception.ConstraintViolationException;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.controlepaciente.cadastrosbasicos.business.ICadastrosBasicosControlePacienteFacade;
import br.gov.mec.aghu.controlepaciente.dao.EcpControlePacienteDAO;
import br.gov.mec.aghu.controlepaciente.dao.EcpHorarioControleDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dominio.DominioTipoMedicaoItemControle;
import br.gov.mec.aghu.dominio.DominioUnidadeMedidaIdade;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AinQuartos;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.EcpControlePaciente;
import br.gov.mec.aghu.model.EcpGrupoControle;
import br.gov.mec.aghu.model.EcpHorarioControle;
import br.gov.mec.aghu.model.EcpItemControle;
import br.gov.mec.aghu.model.EcpLimiteItemControle;
import br.gov.mec.aghu.model.MpmUnidadeMedidaMedica;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.prescricaomedica.cadastrosbasicos.business.ICadastrosBasicosPrescricaoMedicaFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;

/**
 * @author rpetter
 * 
 */
@SuppressWarnings({"PMD.AghuTooManyMethods","PMD.AtributoEmSeamContextManager","PMD.ExcessiveClassLength"})
@Stateless
public class ManterControlesPacienteON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(ManterControlesPacienteON.class);
	
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
	private ICadastrosBasicosPrescricaoMedicaFacade cadastrosBasicosPrescricaoMedicaFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@Inject
	private EcpHorarioControleDAO ecpHorarioControleDAO;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@Inject
	private EcpControlePacienteDAO ecpControlePacienteDAO;
	
	@EJB
	private ICadastrosBasicosControlePacienteFacade cadastrosBasicosControlePacienteFacade;

	@EJB
	private IAmbulatorioFacade ambulatorioFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = 3411841815810920219L;
	String tipoIdade = null;

	public enum ManterControlesPacienteONExceptionCode implements
			BusinessExceptionCode {
		MENSAGEM_PARAM_HORARIO_OBRIG, MENSAGEM_HORARIO_EXISTENTE, MENSAGEM_PARAM_CONTROLE_OBRIG, 
		MENSAGEM_HORARIOPAI_NAO_INFORMADO, MENSAGEM_ITEMCONTROLE_NAO_INFORMADO, MENSAGEM_ITEMCONTROLE_NAO_ENCONTRADO, 
		MENSAGEM_UNIDADEMEDIDA_NAO_ENCONTRADO, MENSAGEM_ATENDIMENTO_NAO_ENCONTRADO, 
		MENSAGEM_PACIENTE_NAO_ENCONTRADO, MENSAGEM_LEITO_NAO_ENCONTRADO, MENSAGEM_QUARTO_NAO_ENCONTRADO,
		MENSAGEM_UNIDADEFUNCIONAL_NAO_ENCONTRADO, MENSAGEM_DATAHORA_FUTURA, MENSAGEM_ERRO_PERSISTIR_DADOS, 
		MENSAGEM_ERRO_HIBERNATE_VALIDATION, MENSAGEM_ERRO_ITEM_FORA_LIMITES_ACEITAVEIS,
		MENSAGEM_ITEM_HORARIO_EXISTENTE, MENSAGEM_SERVIDOR_ALTERACAO_DIFERENTE_INCLUSAO, 
		MENSAGEM_SERVIDOR_OBRIGATORIO, MENSAGEM_PARAMETRO_PRAZO_NAO_DEFINIDO, MENSAGEM_PRAZO_EXCEDIDO_PARA_EXCLUSAO, 
		MENSAGEM_SERVIDOR_EXCLUSAO_DIFERENTE_INCLUSAO, MENSAGEM_ERRO_FORA_PRAZO_PERMITIDO_HORAS_INSERIR,
		MENSAGEM_ERRO_FORA_PRAZO_PERMITIDO_HORAS_ALTERAR, MENSAGEM_INCLUSAO_HORARIO_OUTRO_PROFISSIONAL, 
		MENSAGEM_ALTERACAO_ANOTACAO_OUTRO_PROFISSIONAL
	}

	public EcpHorarioControle obterHorariopelaDataHora(AipPacientes paciente,
			Date dataHora) {
		return this.getEcpHorarioControleDAO().obterHorariopelaDataHora(
				paciente, dataHora);
	}

	public List<EcpControlePaciente> listaControlesHorario( EcpHorarioControle horario, EcpGrupoControle grupo) {
		return ecpControlePacienteDAO.listaControlesHorario(horario, grupo);
	}

	public void gravar(EcpHorarioControle horario, List<EcpControlePaciente> controles) throws BaseException {
        BaseListException listaExcecoes = new BaseListException();
        if (horario != null && horario.getSeq() != null) {
        	this.validaHorarioAlterar(horario);
        }
        for (EcpControlePaciente cont : controles) {
            ManterControlesPacienteON mcp = super.ctx.getBusinessObject(ManterControlesPacienteON.class);
            mcp.criarHorarioSeNecessario(cont);

            if (cont != null && cont.getSeq() != null) {
                if (dadosForamAlterados(cont)) {
                    try {
                        ManterControlesPacienteON mcp1 = super.ctx.getBusinessObject(ManterControlesPacienteON.class);
                        mcp1.alterar(cont);
                    } catch (BaseException e) {
                        listaExcecoes.add(e);
                    }
                }
            } else {
                if (temDadosAInserir(cont)) {
                    //bloco try/catch para persistir os registros corretos. Caso dê exceção, dispara ao final
                    try {
                        ManterControlesPacienteON mcp2 = super.ctx.getBusinessObject(ManterControlesPacienteON.class);
                        mcp2.inserir(cont);
                    } catch (BaseException e) {
                        listaExcecoes.add(e);
                    }
                }
            }
        }
        this.alterar(horario);
        if (listaExcecoes.hasException()) {
            throw listaExcecoes;
        }

	}
	
	/**
	 * Rotina que grava contigência de internação e coloca na fila para geração de pdf
	 * chama package no banco oracle - André Luiz Machado - 11/01/2012
	 * @param horario
	 *  
	 */
	public void dispararGeracaoContigencia(EcpHorarioControle horario)
			throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		AghParametros aghParametro = this.getParametroFacade()
				.buscarAghParametro(
						AghuParametrosEnum.P_GERA_CONTINGENCIA_INTERN);

		if (aghParametro!= null
				&& StringUtils.equals(aghParametro.getVlrTexto(), "S")) {

			this.getEcpHorarioControleDAO().dispararGeracaoContigencia(
					horario.getAtendimento().getSeq(), servidorLogado.getUsuario());

		}
	}	

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void criarHorarioSeNecessario(EcpControlePaciente controlePaciente) throws ApplicationBusinessException {
		// valida associacao com o pai(horario), se não existe inclui
		EcpHorarioControle horario = validaAssociacao(controlePaciente.getHorario());
		if (horario == null) {
			horario = controlePaciente.getHorario();
			this.inserir(horario);
		}
		controlePaciente.setHorario(horario);
	}
	
	private boolean temDadosAInserir(EcpControlePaciente cont) {
		boolean result = false;
		if (cont != null) {
			result = ((cont.getItem().isNumerico() && cont.getMedicao() != null)
					|| (cont.getItem().isTexto()&& StringUtils.isNotBlank(cont.getTexto()))
					|| (cont.getItem().isSimNao() && cont.getSimNao() != null)
					|| (cont.getItem().isInicioFim() && cont.getInicioFim() != null)
					|| (cont.getItem().isMisto() && (cont.getMedicao() != null || cont.getSimNao() != null)))
					|| (cont.getItem().isMistoTexto() && (cont.getMedicao() != null || StringUtils.isNotBlank(cont.getTexto())));
		}
		return result;
	}

	private boolean dadosForamAlterados(EcpControlePaciente cont) {
//		ecpControlePacienteDAO.desatachar(cont);
		boolean existeControleDaBaseIgual = getEcpControlePacienteDAO().existeControleItensAlteracaoIgual(cont);
		return ! existeControleDaBaseIgual; 
	}

	public void alterar(EcpHorarioControle horario) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		// TODO ver validações
		if (anotacaoJaCriadaPorOutroProfissional(horario, servidorLogado)){
			throw new ApplicationBusinessException(ManterControlesPacienteONExceptionCode.MENSAGEM_ALTERACAO_ANOTACAO_OUTRO_PROFISSIONAL);
		}
		
		this.getEcpHorarioControleDAO().merge(horario);
	}
	
	/**
	 * Método que verifica se a anotação foi criada por outro usuário
	 * @param horario
	 * @param servidor
	 * @return
	 */
	private Boolean anotacaoJaCriadaPorOutroProfissional(EcpHorarioControle horario, RapServidores servidor){
		Boolean retorno = false;
		String anotacaoOriginal = getEcpHorarioControleDAO().obterAnotacoesOriginal(horario.getSeq());
		Object[] vetorMatriculaVinculo = getEcpHorarioControleDAO().obterMatriculaVinculoServidorOriginal(horario.getSeq());
		Integer matricula = (Integer)vetorMatriculaVinculo[0];
		Short vinCodigo = (Short)vetorMatriculaVinculo[1];
		if (CoreUtil.modificados(anotacaoOriginal, horario.getAnotacoes())
				&& (!servidor.getId().getMatricula().equals(matricula) || !servidor.getId().getVinCodigo().equals(vinCodigo))) {
			retorno = true;
		}
		return retorno;
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void alterar(EcpControlePaciente controlePaciente) throws ApplicationBusinessException {
		this.valida(controlePaciente);
		this.validaServidorAlteracao(controlePaciente);
		ecpControlePacienteDAO.merge(controlePaciente);
	}

	protected void validaServidorAlteracao(EcpControlePaciente controlePaciente) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		RapServidoresId servidorOriginalId = controlePaciente.getServidor().getId();
		RapServidores servidorOriginal = this.registroColaboradorFacade.obterRapServidor(servidorOriginalId);
		
		RapServidores servidorAtualizacao = servidorLogado; 
		if (servidorOriginal != null && servidorAtualizacao != null) {
			LOG.debug("ManterControlesPacienteON.validaServidorAlteracao(): servidor que incluiu = [" + servidorOriginal + "].");
			LOG.debug("ManterControlesPacienteON.validaServidorAlteracao(): servidor que atualiza = [" + servidorAtualizacao + "].");
			if (! servidorOriginal.getId().equals(servidorAtualizacao.getId())) {
				String sigla = null;
				String nomeOriginal = null;
				if (controlePaciente.getItem() != null) {
					sigla = controlePaciente.getItem().getSigla();
				}
				if (servidorOriginal.getPessoaFisica() != null) {
					nomeOriginal = servidorOriginal.getPessoaFisica().getNome();
				}
				throw new ApplicationBusinessException(ManterControlesPacienteONExceptionCode.MENSAGEM_SERVIDOR_ALTERACAO_DIFERENTE_INCLUSAO, sigla, nomeOriginal);
			}
		} else {
			throw new ApplicationBusinessException(ManterControlesPacienteONExceptionCode.MENSAGEM_SERVIDOR_OBRIGATORIO);
		}
		
	}

	public void inserir(EcpHorarioControle horario) throws ApplicationBusinessException {
		
		if (horario == null) {
			throw new ApplicationBusinessException(ManterControlesPacienteONExceptionCode.MENSAGEM_PARAM_HORARIO_OBRIG);
		}

		validaHorarioInserir(horario);

		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		// auditoria
		horario.setCriadoEm(new Date());
		if (horario.getServidor() == null) {
			horario.setServidor(servidorLogado);
		}
		
		// atribui demais associações
		if(horario.getAtendimento() != null) {
			horario.setPaciente(horario.getAtendimento().getPaciente());
			horario.setUnidadeFuncional(horario.getAtendimento().getUnidadeFuncional());
			horario.setLeito(horario.getAtendimento().getLeito());
			horario.setQuarto(horario.getAtendimento().getQuarto());
			// valida outras associações
			horario.setAtendimento(validaAssociacao(horario.getAtendimento()));
		}
		horario.setPaciente(validaAssociacao(horario.getPaciente()));
		horario.setUnidadeFuncional(validaAssociacao(horario.getUnidadeFuncional()));
		horario.setQuarto(validaAssociacao(horario.getQuarto()));
		horario.setLeito(validaAssociacao(horario.getLeito()));

		try {
			// Para casos onde na primeira tentativa ocorreu erro, em algum momento e tenha feito as alteraçẽos.
			if(horario.getSeq() != null){
				ecpHorarioControleDAO.merge(horario);
				
			} else {
				ecpHorarioControleDAO.persistir(horario);
			}
				
			ecpHorarioControleDAO.flush();
		} catch (Exception e) {
			LOG.error("Exceção capturada: ", e);
			if (e.getCause() != null
					&& ConstraintViolationException.class.equals(e.getCause().getClass())) {

				if (StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause()).getConstraintName(), "AGH.ECP_HCT_UK1")) {
					throw new ApplicationBusinessException(ManterControlesPacienteONExceptionCode.MENSAGEM_HORARIO_EXISTENTE);
				}
				
				if (StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause()).getConstraintName(), "AGH.ECP_RCP_UK1")) {
					throw new ApplicationBusinessException(ManterControlesPacienteONExceptionCode.MENSAGEM_ITEM_HORARIO_EXISTENTE);
				}
			}
		}
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void inserir(EcpControlePaciente controlePaciente) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		if (controlePaciente == null) {
			throw new ApplicationBusinessException(ManterControlesPacienteONExceptionCode.MENSAGEM_PARAM_CONTROLE_OBRIG);
		}

		valida(controlePaciente);

		// auditoria
		controlePaciente.setCriadoEm(new Date());
		controlePaciente.setServidor(servidorLogado);

		// valida item associado
		controlePaciente.setItem(validaAssociacao(controlePaciente.getItem()));

		// guarada a medição padrão do item no momento, em caso de futura alteração
		controlePaciente.setUnidadeMedida(controlePaciente.getItem().getUnidadeMedidaMedica());
		
		// valida unidade de medida associada
		controlePaciente.setUnidadeMedida(validaAssociacao(controlePaciente
				.getUnidadeMedida()));
				
		if (existeHorarioParaOutroUsuario(controlePaciente.getHorario())){
			throw new ApplicationBusinessException(ManterControlesPacienteONExceptionCode.MENSAGEM_INCLUSAO_HORARIO_OUTRO_PROFISSIONAL);
		}

		try {
			ecpControlePacienteDAO.persistir(controlePaciente);
			ecpControlePacienteDAO.flush();
		} catch (javax.validation.ConstraintViolationException ise) {
			String mensagem = "";
			Set<ConstraintViolation<?>> arr = ise.getConstraintViolations();
			for (ConstraintViolation item : arr) {
				if (!"".equals(item)) {
					mensagem = item.getMessage();
					if (mensagem.isEmpty()) {
						mensagem = " Valor inválido para o campo "+ item.getPropertyPath();
					}
				}
			}
			throw new ApplicationBusinessException(ManterControlesPacienteONExceptionCode.MENSAGEM_ERRO_HIBERNATE_VALIDATION,mensagem);

		} catch (Exception e) {
			throw new ApplicationBusinessException(ManterControlesPacienteONExceptionCode.MENSAGEM_ERRO_PERSISTIR_DADOS,e.getMessage());
		}
	}
	
	public void excluirRegistroControlePaciente(Long seqHorarioControle) throws ApplicationBusinessException {
		if (seqHorarioControle == null) {
			throw new IllegalArgumentException();
		}
		EcpHorarioControle horarioControle = getEcpHorarioControleDAO().obterPorChavePrimaria(seqHorarioControle);
		excluir(horarioControle);
	}

	public void excluir(EcpControlePaciente controlePaciente)throws ApplicationBusinessException {
		if (controlePaciente == null) {
			throw new IllegalArgumentException(ManterControlesPacienteONExceptionCode.MENSAGEM_PARAM_CONTROLE_OBRIG.toString());
		}
		
		// Para atachar na sessao
		controlePaciente = ecpControlePacienteDAO.obterPorChavePrimaria(controlePaciente.getSeq());
		
		EcpHorarioControle horario = controlePaciente.getHorario();
		validarExclusao(horario);
		ecpControlePacienteDAO.remover(controlePaciente);
		ecpControlePacienteDAO.flush();

		// se era o último controle existente apaga também o horário
		List<EcpControlePaciente> restantes = ecpControlePacienteDAO.listaControlesHorario(horario, null);
		if (restantes.isEmpty()) {
			ecpHorarioControleDAO.remover(horario);
			ecpHorarioControleDAO.flush();
		}

	}

	public void excluir(EcpHorarioControle horarioControle) throws ApplicationBusinessException {
		if (horarioControle == null) {
			throw new IllegalArgumentException(ManterControlesPacienteONExceptionCode.MENSAGEM_PARAM_HORARIO_OBRIG.toString());
		}
		
		horarioControle = ecpHorarioControleDAO.obterPorChavePrimaria(horarioControle.getSeq());
		
		this.validarExclusao(horarioControle);

		// primeiro remove todos os controles d horário, apesar do cascade
		// estava dando erro em algumas situações no banco postgres
		List<EcpControlePaciente> listaControles = ecpControlePacienteDAO.listaControlesHorario(horarioControle, null);
		for (EcpControlePaciente controle : listaControles) {
			ecpControlePacienteDAO.remover(controle);
			ecpControlePacienteDAO.flush();
		}
		ecpHorarioControleDAO.refresh(horarioControle);
		ecpHorarioControleDAO.remover(horarioControle);
	}

	private void validarExclusao(EcpHorarioControle horarioControle) throws ApplicationBusinessException {
		this.validarParametroExclusao(horarioControle);
		this.validarServidorExclusao(horarioControle);
	}

	/**
	 * Verifica prazo de número de horas  permitidas para exclusão no parâmetro P_AGHU_PRAZO_EXCLUI_CONTROLES
	 * @param horarioControle
	 *  
	 */
	protected void validarParametroExclusao(EcpHorarioControle horarioControle) throws ApplicationBusinessException {
		int parametroExclusao = obterParametroPrazoExclusao();
		LOG.info("ManterControlesPacienteON.validarParametroExclusao(): P_AGHU_PRAZO_EXCLUI_CONTROLES = [" + parametroExclusao + "].");
		Date agora = Calendar.getInstance().getTime();
		Date dataControleAjustadoParametro = DateUtils.addHours(horarioControle.getDataHora(), parametroExclusao);
		if (agora.after(dataControleAjustadoParametro)) {
			throw new ApplicationBusinessException(
					ManterControlesPacienteONExceptionCode.MENSAGEM_PRAZO_EXCEDIDO_PARA_EXCLUSAO, parametroExclusao);
		}
	}
	
	/**
	 * Realiza a busca do parâmetro que informa o prazo para exclusão de
	 * horários
	 * 
	 * @return
	 * @throws AGHUBaseException
	 */
	protected int obterParametroPrazoExclusao() throws ApplicationBusinessException {

		AghParametros aghParametro = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_AGHU_PRAZO_EXCLUI_CONTROLES);

		if (aghParametro == null || aghParametro.getVlrNumerico() == null) {
			throw new ApplicationBusinessException(
					ManterControlesPacienteONExceptionCode.MENSAGEM_PARAMETRO_PRAZO_NAO_DEFINIDO);
		}
		return aghParametro.getVlrNumerico().intValue();

	}
	
	protected void validarServidorExclusao(EcpHorarioControle horarioControle) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		if (horarioControle == null) {
			throw new IllegalArgumentException();
		}
		RapServidores servidorOriginal = horarioControle.getServidor();
		RapServidores servidorAtualizacao = servidorLogado; 
		if (servidorOriginal != null && servidorAtualizacao != null) {
			LOG.debug("ManterControlesPacienteON.validarServidorExclusao(): servidor que incluiu = [" + servidorOriginal + "].");
			LOG.debug("ManterControlesPacienteON.validarServidorExclusao(): servidor que exclui = [" + servidorAtualizacao + "].");
			if (! servidorOriginal.getId().equals(servidorAtualizacao.getId())) {
				String nomeOriginal = null;
				if (servidorOriginal.getPessoaFisica() != null) {
					nomeOriginal = servidorOriginal.getPessoaFisica().getNome();
				}
				throw new ApplicationBusinessException(ManterControlesPacienteONExceptionCode.MENSAGEM_SERVIDOR_EXCLUSAO_DIFERENTE_INCLUSAO, nomeOriginal);
			}
		} else {
			throw new ApplicationBusinessException(ManterControlesPacienteONExceptionCode.MENSAGEM_SERVIDOR_OBRIGATORIO);
		}
		
	}
	
	private void valida(EcpControlePaciente controlePaciente) throws ApplicationBusinessException {
		// validaões de negócio
		// E07 – Item de controle fora dos limites aceitáveis - gera Erro
		this.verificaItemForaLimitesErro(controlePaciente.getHorario(), controlePaciente);

		// E08 – Item de controle fora dos limites de normalidade - gera
		// Mensagem de Alerta na controles
		if (this.verificaItemForaLimitesNormalidade(controlePaciente.getHorario(), controlePaciente)) {
			controlePaciente.setForaLimiteNormal(true);
		} else {
			controlePaciente.setForaLimiteNormal(false);
		}
	}

	private void validaDataFutura(EcpHorarioControle horario) throws ApplicationBusinessException {
		Date dataAtual = new Date();
		if (dataAtual.before(horario.getDataHora())) {
			throw new ApplicationBusinessException(
					ManterControlesPacienteONExceptionCode.MENSAGEM_DATAHORA_FUTURA);
		}
	}
	
	private void validaHorarioInserir(EcpHorarioControle horario) throws ApplicationBusinessException {
		validaDataFutura(horario);
		// E05– Data/hora informada anterior ao prazo permitido para 
		// inclusão/alteração de controles
		verificaForaPrazoPermitidoHorasInserir(horario);
	}
	
	private void validaHorarioAlterar(EcpHorarioControle horario) throws ApplicationBusinessException {
		validaDataFutura(horario);
		// E05– Data/hora informada anterior ao prazo permitido para 
		// inclusão/alteração de controles
		verificaForaPrazoPermitidoHorasAlterar(horario);
	}
	
	/**
	 * Método que verifica se o horário cujo item foi inserido foi criado por outro usuário
	 * @param horario
	 * @return
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	private Boolean existeHorarioParaOutroUsuario(EcpHorarioControle horario)
			throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		Boolean retorno = false;
		List<EcpHorarioControle> listaHorariosInclusos = getEcpHorarioControleDAO().pesquisarHorarioControlePorPacienteUsuario(
				 horario.getPaciente(), horario.getDataHora());
		if (listaHorariosInclusos != null && !listaHorariosInclusos.isEmpty()) {
			for (EcpHorarioControle e : listaHorariosInclusos) {
				if (!e.getServidor().getId().equals(servidorLogado.getId())) {
					retorno = true;
					break;
				}
			}
		}	
		return retorno;
	}

	/**
	 * Verifica se o horário ja existe!
	 * 
	 * @param horarioControle
	 * @return horarioControle
	 * @throws ApplicationBusinessException
	 */
	private EcpHorarioControle validaAssociacao(EcpHorarioControle horarioControle) throws ApplicationBusinessException {
		EcpHorarioControle result = null;
		if (horarioControle == null) {
			throw new ApplicationBusinessException(ManterControlesPacienteONExceptionCode.MENSAGEM_HORARIOPAI_NAO_INFORMADO);
		}
		// se possui dieta associada carrega
		if (horarioControle.getSeq() != null) {
			result = ecpHorarioControleDAO.obterPorChavePrimaria(horarioControle.getSeq());
		}
		return result;
	}

	/**
	 * Valida o Tipo de Item de controle associado ao Item
	 * 
	 * @param itemControle
	 * @return
	 * @throws ApplicationBusinessException
	 */
	private EcpItemControle validaAssociacao(EcpItemControle item)
			throws ApplicationBusinessException {

		EcpItemControle result = null;

		if (item == null || item.getSeq() == null) {
			throw new ApplicationBusinessException(
					ManterControlesPacienteONExceptionCode.MENSAGEM_ITEMCONTROLE_NAO_INFORMADO);
		}

		// se possui item associado carrega
		result = this.getCadastrosBasicosControlePacienteFacade().obterItemControlePorId(item.getSeq());
		if (result == null) {
			throw new ApplicationBusinessException(
					ManterControlesPacienteONExceptionCode.MENSAGEM_ITEMCONTROLE_NAO_ENCONTRADO);
		}
		return result;
	}

	/**
	 * Valida a asscoiação com paciente
	 * 
	 * @param paciente
	 * @return
	 * @throws ApplicationBusinessException
	 */
	private AipPacientes validaAssociacao(AipPacientes paciente)
			throws ApplicationBusinessException {

		AipPacientes result = null;

		// se possui paciente associado carrega
		if (paciente != null && paciente.getCodigo() != null) {
			result = this.getPacienteFacade().obterPacientePorCodigo(
					paciente.getCodigo());
			if (result == null) {
				throw new ApplicationBusinessException(
						ManterControlesPacienteONExceptionCode.MENSAGEM_PACIENTE_NAO_ENCONTRADO);
			}
		}
		return result;
	}

	/**
	 * Valida a asscoiação com atendimento
	 * 
	 * @param atendimento
	 * @return
	 * @throws ApplicationBusinessException
	 */
	private AghAtendimentos validaAssociacao(AghAtendimentos atendimento)
			throws ApplicationBusinessException {

		AghAtendimentos result = null;

		// se possui atendimento associado carrega
		if (atendimento != null && atendimento.getSeq() != null) {
			result = this.getAghuFacade().obterAghAtendimentoPorChavePrimaria(atendimento.getSeq());
			if (result == null) {
				throw new ApplicationBusinessException(
						ManterControlesPacienteONExceptionCode.MENSAGEM_ATENDIMENTO_NAO_ENCONTRADO);
			}
		}
		return result;
	}

	/**
	 * Valida a asscoiação com unidade funcional
	 * 
	 * @param unidadeFuncional
	 * @return
	 * @throws ApplicationBusinessException
	 */
	private AghUnidadesFuncionais validaAssociacao(
			AghUnidadesFuncionais unidadeFuncional) throws ApplicationBusinessException {

		AghUnidadesFuncionais result = null;

		// se possui unidade funcional associado carrega
		if (unidadeFuncional != null && unidadeFuncional.getSeq() != null) {
			result = this.getAghuFacade()
					.obterAghUnidadesFuncionaisPorChavePrimaria(
							unidadeFuncional.getSeq());
			if (result == null) {
				throw new ApplicationBusinessException(
						ManterControlesPacienteONExceptionCode.MENSAGEM_UNIDADEFUNCIONAL_NAO_ENCONTRADO);
			}
		}
		return result;
	}

	/**
	 * Valida a asscoiação com quarto
	 * 
	 * @param quarto
	 * @return
	 * @throws ApplicationBusinessException
	 */
	private AinQuartos validaAssociacao(AinQuartos quarto)
			throws ApplicationBusinessException {

		AinQuartos result = null;

		// se possui quarto associado carrega
		if (quarto != null && quarto.getNumero() != null) {
			result = this.getInternacaoFacade().obterQuartoPorId(quarto.getNumero());
			if (result == null) {
				throw new ApplicationBusinessException(
						ManterControlesPacienteONExceptionCode.MENSAGEM_QUARTO_NAO_ENCONTRADO);
			}
		}
		return result;
	}

	/**
	 * Valida a asscoiação com leito
	 * 
	 * @param leito
	 * @return
	 * @throws ApplicationBusinessException
	 */
	private AinLeitos validaAssociacao(AinLeitos leito)
			throws ApplicationBusinessException {

		AinLeitos result = null;

		// se leito associado carrega
		if (leito != null && leito.getLeitoID() != null) {
			result = this.getInternacaoFacade().obterLeitoPorId(
					leito.getLeitoID());
			if (result == null) {
				throw new ApplicationBusinessException(
						ManterControlesPacienteONExceptionCode.MENSAGEM_LEITO_NAO_ENCONTRADO);
			}
		}
		return result;
	}

	/**
	 * Valida a asscoiação com unidade de medida
	 * 
	 * @param unidadeMedida
	 * @return
	 * @throws ApplicationBusinessException
	 */
	private MpmUnidadeMedidaMedica validaAssociacao(
			MpmUnidadeMedidaMedica unidadeMedida) throws ApplicationBusinessException {

		MpmUnidadeMedidaMedica result = null;

		if (unidadeMedida != null && unidadeMedida.getSeq() != null) {
			// busca unidade no banco
			result = this.getCadastrosBasicosPrescricaoMedicaFacade()
					.obterUnidadeMedidaMedicaPorId(unidadeMedida.getSeq());
			if (result == null) {
				throw new ApplicationBusinessException(
						ManterControlesPacienteONExceptionCode.MENSAGEM_UNIDADEMEDIDA_NAO_ENCONTRADO);
			}
		}

		return result;
	}

	/**
	 * RN05 Calcula idade em anos do paciente
	 * 
	 * @param paciente
	 * @return
	 */
	private Integer calculaIdadePacienteAnos(AipPacientes paciente) {		
		return DateUtil.getIdade(paciente.getDtNascimento());
	}

	/**
	 * RN05 Calcula idade em meses do paciente
	 * 
	 * @param paciente
	 * @return
	 */
	private Integer calculaIdadePacienteMeses(AipPacientes paciente) {
		return DateUtil.getIdadeMeses(paciente.getDtNascimento());
	}

	/**
	 * RN05 Calcula idade em dias do paciente
	 * 
	 * @param paciente
	 * @return
	 */
	private Integer calculaIdadePacienteDias(AipPacientes paciente) {
		return DateUtil.getIdadeDias(paciente.getDtNascimento());
		
	}

	/**
	 * RN06 - Busca limites do item de controle
	 * 
	 * @param horario
	 * @param controlePaciente
	 * @return
	 */
	private EcpLimiteItemControle buscaLimitesErroNormalidade(EcpHorarioControle horario, EcpControlePaciente controlePaciente) {
		EcpLimiteItemControle limiteItemControle = null;
		// calcula idade em anos
		AipPacientes paciente = null;
		if(horario.getAtendimento() != null) {
			AghAtendimentos atd = aghuFacade.obterAghAtendimentoPorChavePrimaria(horario.getAtendimento().getSeq());
			paciente = atd.getPaciente();
		} else {
			paciente = pacienteFacade.obterPaciente(horario.getPaciente().getCodigo()); // evita lazy na dtNascimento
		}
		Integer idade = calculaIdadePacienteAnos(paciente);
		DominioUnidadeMedidaIdade medidaIdade = DominioUnidadeMedidaIdade.A;
		// se idade em anos > 0 busca limites para unidade de medida = A
		if (idade > 0) {
			limiteItemControle =getCadastrosBasicosControlePacienteFacade()
					.buscaLimiteItemControle(controlePaciente.getItem(),
							medidaIdade, idade);
		} else {
			// se idade em Anos = zero , calcula idade em Meses
			idade = calculaIdadePacienteMeses(paciente);
			medidaIdade = DominioUnidadeMedidaIdade.M;
			// se idade em meses > 0 busca limites para unidade de medida = M
			if (idade > 0) {
				limiteItemControle = getCadastrosBasicosControlePacienteFacade()
						.buscaLimiteItemControle(controlePaciente.getItem(),
								medidaIdade, idade);
				// se não encontrou limites do item mensal
				// procura Anual com idade = 0
				if (limiteItemControle == null) {
					idade = 0;
					medidaIdade = DominioUnidadeMedidaIdade.A;
					limiteItemControle = getCadastrosBasicosControlePacienteFacade()
							.buscaLimiteItemControle(
									controlePaciente.getItem(), medidaIdade,
									idade);
				}
			} else {
				// se idade em Meses = zero , calcula idade em Dias
				idade = calculaIdadePacienteDias(paciente);
				medidaIdade = DominioUnidadeMedidaIdade.D;
				limiteItemControle = getCadastrosBasicosControlePacienteFacade()
						.buscaLimiteItemControle(controlePaciente.getItem(),
								medidaIdade, idade);
				// se não encontrou limites do item Diario
				// procura Mensal com idade = 0
				if (limiteItemControle == null) {
					idade = 0;
					medidaIdade = DominioUnidadeMedidaIdade.M;
					limiteItemControle = getCadastrosBasicosControlePacienteFacade()
							.buscaLimiteItemControle(
									controlePaciente.getItem(), medidaIdade,
									idade);
				}

				// se não encontrou limites do item mensal
				// procura Anual com idade = 0
				if (limiteItemControle == null) {
					idade = 0;
					medidaIdade = DominioUnidadeMedidaIdade.A;
					limiteItemControle = getCadastrosBasicosControlePacienteFacade()
							.buscaLimiteItemControle(
									controlePaciente.getItem(), medidaIdade,
									idade);
				}
			}
		}
		return limiteItemControle;

	}

	private Integer calculaIdadePaciente(AipPacientes paciente) {
		Integer idade;
		idade = calculaIdadePacienteAnos(paciente);
		if (idade > 0) {
			this.tipoIdade = (idade > 1) ? " anos" : " ano";
			return idade;
		} else {
			idade = calculaIdadePacienteMeses(paciente);
			if (idade > 0) {
				this.tipoIdade = (idade > 1) ? " meses" : " mes";
				return idade;
			} else {
				idade = calculaIdadePacienteDias(paciente);
				if (idade > 0) {
					this.tipoIdade = (idade > 1) ? " dias" : " dia";
					return idade;
				}
			}
		}
		return 0;
	}
	
	private void verificaForaPrazoPermitidoHorasInserir(EcpHorarioControle horario)
			throws ApplicationBusinessException {
		int prazoHoras = retornaValorNumeroParametro(AghuParametrosEnum.P_HORAS_INCLUIR_CONTROLES);
		Integer quantidadeHorasControleHoraAtual = DateUtil.obterQtdHorasEntreDuasDatas(horario.getDataHora(),
				new Date());
		if (foraPrazoPermitidoItensControle(prazoHoras, quantidadeHorasControleHoraAtual)) {
			throw new ApplicationBusinessException(
					ManterControlesPacienteONExceptionCode.MENSAGEM_ERRO_FORA_PRAZO_PERMITIDO_HORAS_INSERIR,
					prazoHoras);
		}
	}
	
	private void verificaForaPrazoPermitidoHorasAlterar(EcpHorarioControle horario) throws ApplicationBusinessException {
		int prazoHoras = retornaValorNumeroParametro(AghuParametrosEnum.P_HORAS_ALTERAR_CONTROLES);
		Integer quantidadeHorasControleHoraAtual = DateUtil.obterQtdHorasEntreDuasDatas(horario.getDataHora(),
				new Date());
		if (foraPrazoPermitidoItensControle(prazoHoras, quantidadeHorasControleHoraAtual)) {
			throw new ApplicationBusinessException(
					ManterControlesPacienteONExceptionCode.MENSAGEM_ERRO_FORA_PRAZO_PERMITIDO_HORAS_ALTERAR,
					prazoHoras);
		}
	}
	
	private Boolean foraPrazoPermitidoItensControle(int prazoHoras, Integer quantidadeHorasControleHoraAtual) {
		return quantidadeHorasControleHoraAtual > prazoHoras;
	}
	
	private Integer retornaValorNumeroParametro(AghuParametrosEnum nomeParametro) throws ApplicationBusinessException {
		AghParametros param = getParametroFacade().buscarAghParametro(nomeParametro);
		return param.getVlrNumerico().intValue();
	}
	
			

	
	/**
	 * E07 – Item de controle fora dos limites aceitáveis - gera Erro
	 */
	private void verificaItemForaLimitesErro(EcpHorarioControle horario,
			EcpControlePaciente controlePaciente) throws ApplicationBusinessException {

		if (DominioTipoMedicaoItemControle.NU.equals(controlePaciente.getItem()
				.getTipoMedicao())) {
			EcpLimiteItemControle limiteItemControle = buscaLimitesErroNormalidade(
					horario, controlePaciente);
			if ((controlePaciente != null)
					&& (controlePaciente.getMedicao() != null)
					&& (limiteItemControle != null)
					&& (limiteItemControle.getLimiteInferiorErro() != null)
					&& (limiteItemControle.getLimiteSuperiorErro() != null)) {
				if ((controlePaciente.getMedicao().compareTo(
						limiteItemControle.getLimiteInferiorErro()) == -1)
						|| (controlePaciente.getMedicao().compareTo(
								limiteItemControle.getLimiteSuperiorErro()) == 1)) {
					throw new ApplicationBusinessException(
							ManterControlesPacienteONExceptionCode.MENSAGEM_ERRO_ITEM_FORA_LIMITES_ACEITAVEIS,
							controlePaciente.getItem().getSigla(),
							controlePaciente.getItem().getDescricao(),
							limiteItemControle.getLimiteInferiorErro(),
							limiteItemControle.getLimiteSuperiorErro());
				}
			}

		}
	}

	/**
	 * E08 – Item de controle fora dos limites de normalidade - gera Mensagem de
	 * Alerta
	 */
	public boolean verificaItemForaLimitesNormalidade(
			EcpHorarioControle horario, EcpControlePaciente controlePaciente)
			throws ApplicationBusinessException {

		if (DominioTipoMedicaoItemControle.NU.equals(controlePaciente.getItem()
				.getTipoMedicao())) {
			EcpLimiteItemControle limiteItemControle = buscaLimitesErroNormalidade(
					horario, controlePaciente);
			if ((controlePaciente != null)
					&& (controlePaciente.getMedicao() != null)
					&& (limiteItemControle != null)
					&& (limiteItemControle.getLimiteInferiorNormal() != null)
					&& (limiteItemControle.getLimiteSuperiorNormal() != null)) {
				if ((controlePaciente.getMedicao().compareTo(
						limiteItemControle.getLimiteInferiorNormal()) == -1)
						|| (controlePaciente.getMedicao().compareTo(
								limiteItemControle.getLimiteSuperiorNormal()) == 1)) {
					return true;

				}
			}
		}
		return false;
	}
	
	/**
	 * E08 – Item de controle fora dos limites de normalidade - gera Mensagem de
	 * Alerta Mensagem de alerta que será mostrada na classe controller
	 */
	public String mensagemItemForaLimitesNormalidade(EcpHorarioControle horario, EcpControlePaciente controlePaciente) throws ApplicationBusinessException {
		EcpHorarioControle auxHorario = ecpHorarioControleDAO.obterPorChavePrimaria(horario.getSeq());
		//EcpControlePaciente auxControlePaciente = controlePaciente;
		
		String retorno = null;
		if (this.verificaItemForaLimitesNormalidade(auxHorario, controlePaciente)) {
			AipPacientes paciente = null;
			if(auxHorario.getAtendimento() != null) {
				AghAtendimentos atd = aghuFacade.obterAghAtendimentoPorChavePrimaria(auxHorario.getAtendimento().getSeq());
				paciente = atd.getPaciente();
			} else {
				paciente = pacienteFacade.obterPaciente(auxHorario.getPaciente().getCodigo()); // evita lazy na dtNascimento
			}
			Integer idade = calculaIdadePaciente(paciente);
			EcpLimiteItemControle limiteItemControle = buscaLimitesErroNormalidade(auxHorario, controlePaciente);
			
			retorno = controlePaciente.getItem().getSigla() + " - "
					+ controlePaciente.getItem().getDescricao()
					+ " fora dos limites de normalidade de "
					+ limiteItemControle.getLimiteInferiorNormal() + " até "
					+ limiteItemControle.getLimiteSuperiorNormal()
					+ " para paciente com idade = " + idade + this.tipoIdade
					+ " . Registre Anotações.";

		}
		return retorno;
	}
	
	public boolean validaUnidadeFuncionalInformatizada(
			AghAtendimentos atendimento, Short unfSeq) {
		return this
				.getAghuFacade()
				.unidadeFuncionalPossuiCaracteristica(
						unfSeq != null ? unfSeq : atendimento.getUnidadeFuncional().getSeq(),
						ConstanteAghCaractUnidFuncionais.CONTROLES_PACIENTE_INFORMATIZADO);
	}

	public List<EcpControlePaciente> obterUltimosDadosControlePacientePelaConsulta(Integer numeroConsulta) {
		List<EcpControlePaciente> lista = getEcpControlePacienteDAO().listarUltimosDadosControlePacientePelaConsulta(numeroConsulta);
		List<EcpControlePaciente> resultado = new ArrayList<>();
		AacConsultas consulta = ambulatorioFacade.obterAacConsulta(numeroConsulta);
		for(EcpControlePaciente controle : lista) {
			if(DateUtil.entre(controle.getHorario().getDataHora(),  DateUtil.adicionaHoras(consulta.getDtConsulta(), -1), consulta.getDtConsulta())) {
				resultado.add(controle);
			}
		}
		
		return resultado;
	}

	public List<EcpControlePaciente> obterUltimosDadosControlePacientePelaCodigoPaciente(Integer pacCodigo) {
		List<EcpControlePaciente> lista = getEcpControlePacienteDAO().listarUltimosDadosControlePacientePeloCodigoPaciente(pacCodigo);
		return lista;
	}

	// DAOs
	protected EcpControlePacienteDAO getEcpControlePacienteDAO() {
		return ecpControlePacienteDAO;
	}

	protected EcpHorarioControleDAO getEcpHorarioControleDAO() {
		return ecpHorarioControleDAO;
	}

	// Facades
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}
	
	protected IPacienteFacade getPacienteFacade(){
		return pacienteFacade;
	}
	
	protected IRegistroColaboradorFacade getRegistroColaboradorFacade(){
		return registroColaboradorFacade;
	}	

	protected ICadastrosBasicosControlePacienteFacade getCadastrosBasicosControlePacienteFacade(){
		return cadastrosBasicosControlePacienteFacade;
	}

	protected ICadastrosBasicosPrescricaoMedicaFacade getCadastrosBasicosPrescricaoMedicaFacade(){
		return cadastrosBasicosPrescricaoMedicaFacade;
	}

	protected IInternacaoFacade getInternacaoFacade() {
		return this.internacaoFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}

	public void atualizarAtdSeqHorarioControlePaciente(Integer atdSeq, Long trgSeq) throws ApplicationBusinessException{
		List<EcpHorarioControle> controles = this.ecpHorarioControleDAO.listarHorarioControlePorTriagem(trgSeq);
		
		if (controles != null && !controles.isEmpty()){
			AghAtendimentos aghAtendimento = this.aghuFacade.obterAghAtendimentoPorChavePrimaria(atdSeq);
			
			for (EcpHorarioControle ecpHorarioControle: controles){
				ecpHorarioControle.setAtendimento(aghAtendimento);
				this.alterar(ecpHorarioControle);
			}
		}
	}
		
}
