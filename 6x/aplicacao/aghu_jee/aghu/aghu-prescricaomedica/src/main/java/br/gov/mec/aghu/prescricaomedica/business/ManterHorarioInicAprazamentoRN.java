package br.gov.mec.aghu.prescricaomedica.business;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MpmHorarioInicAprazamentJn;
import br.gov.mec.aghu.model.MpmHorarioInicAprazamento;
import br.gov.mec.aghu.model.MpmTipoFrequenciaAprazamento;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.dao.MpmHorarioInicAprazamentJnDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmHorarioInicAprazamentoDAO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ManterHorarioInicAprazamentoRN extends BaseBusiness{

	private static final Log LOG = LogFactory.getLog(ManterHorarioInicAprazamentoRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private MpmHorarioInicAprazamentoDAO mpmHorarioInicAprazamentoDAO;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@Inject
	private MpmHorarioInicAprazamentJnDAO mpmHorarioInicAprazamentJnDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 6473730879692208738L;


	public enum ManterHorarioInicAprazamentoRNExceptionCode implements BusinessExceptionCode {
		
		MPM_00766,
		MPM_01274,
		MPM_00777,
		MPM_00906,
		ERRO_HORARIO_APRAZAMENTO_JA_EXISTENTE,
		ERRO_HORARIO_INICIO_OBRIGATORIO,
		ERRO_TIPO_FREQUENCIA_APRAZAMENTO_OBRIGATORIO, 
		ERRO_UNIDADE_FUNCIONAL_OBRIGATORIA;

	}
	
	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}
	
	protected MpmHorarioInicAprazamentoDAO getMpmHorarioInicAprazamentoDAO(){
		
		return mpmHorarioInicAprazamentoDAO;
	}
	
	protected MpmHorarioInicAprazamentJnDAO getMpmHorarioInicAprazamentJnDAO(){
		
		return mpmHorarioInicAprazamentJnDAO;
	}
	
	/**
	 * Efetua validações necessárias para persistência (inclusão) do horário de aprazamento.
	 * RN03 e RN04 de MPMT_HIA_BRI
	 * @param horarioAprazamento
	 * @throws BaseListException 
	 */
	private void validarHorarioAprazamentoInclusao(
			MpmHorarioInicAprazamento horarioAprazamento) throws BaseListException {
		BaseListException excecoes = new BaseListException();
		
		verificarObrigadoriedadeUnidadeFuncional(horarioAprazamento.getUnidadeFuncional(), excecoes);
		//RN03
		verificarUnidadeFuncionalHorarioAprazamento(horarioAprazamento.getUnidadeFuncional(), excecoes);
		verificarObrigatoriedadeFrequenciaAprazamento(horarioAprazamento.getTipoFreqAprazamento(), excecoes);
		//RN04
		verificarFrequenciaAprazamento(horarioAprazamento.getTipoFreqAprazamento(), excecoes);
		verificarObrigatoriedadeHorarioInicio(horarioAprazamento.getHorarioInicio(), excecoes);
		
		if(excecoes.hasException()){
			throw excecoes;
		}
	}
	
	/**
	 * Executa as RN02 e RN03 de MPMT_HIA_BRU
	 * @param horarioAprazamento
	 * @throws BaseListException 
	 */
	private void validarHorarioAprazamentoAlteracao(
			MpmHorarioInicAprazamento horarioAprazamento, MpmHorarioInicAprazamento horarioAprazamentoOriginal) throws BaseListException {
		BaseListException excecoes = new BaseListException();
		
		verificarObrigadoriedadeUnidadeFuncional(horarioAprazamento.getUnidadeFuncional(), excecoes);
		//RN02
		this.verificarAlteracaoUnidadeFuncional(horarioAprazamento, horarioAprazamentoOriginal, excecoes);
		verificarObrigatoriedadeFrequenciaAprazamento(horarioAprazamento.getTipoFreqAprazamento(), excecoes);
		//RN03
		this.verificarAlteracaoTipoFrequenciaAprazamento(horarioAprazamento, horarioAprazamentoOriginal, excecoes);
		verificarObrigatoriedadeHorarioInicio(horarioAprazamento.getHorarioInicio(), excecoes);
		
		if(excecoes.hasException()){
			throw excecoes;
		}
	}
	
	/**
	 * Código no início da trigger MPMT_HIA_BRI
	 * Insere data de criação - RN01
	 * @param horarioAprazamento
	 * @author clayton.bras
	 */
	private void atualizarDataCriacao(MpmHorarioInicAprazamento horarioAprazamento){
		if(horarioAprazamento!=null){
			horarioAprazamento.setCriadoEm(new Date());
		}
	}
	
	/**
	 * Código no final da trigger MPMT_HIA_BRI (RN01) / MPMT_HIA_BRU (RN01)
	 * Insere servidor criador/ atualizador
	 * @param horarioAprazamento
	 * @author clayton.bras
	 * @throws ApplicationBusinessException  
	 */
	private void atualizarServidor(MpmHorarioInicAprazamento horarioInicAprazamento) throws ApplicationBusinessException{
		if(horarioInicAprazamento!=null){
			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
			
			horarioInicAprazamento.setServidor(servidorLogado);
		}
	}
	
	/**
	 * @ORADB: MPMK_HIA_RN.RN_HIAP_VER_UNF
	 * Verifica unidade funcional quanto a situação e características
	 * @param horarioAprazamento
	 * @param excecoes
	 * @author clayton.bras
	 */
	private void verificarUnidadeFuncionalHorarioAprazamento(AghUnidadesFuncionais unidadeFuncionalHorarioAprazamento, BaseListException excecoes){
		if(unidadeFuncionalHorarioAprazamento !=null){ 
			if(!DominioSituacao.A.equals(unidadeFuncionalHorarioAprazamento.getIndSitUnidFunc())){
				excecoes.add(new ApplicationBusinessException(ManterHorarioInicAprazamentoRNExceptionCode.MPM_00766));
			}
			/*#40018
			//Verifica se a unidade funcional possui uma das características: Unid Emergencia, Unid Internacao, 'Unid Hosp Dia'. 
			//Caso possua alguma dessas, sequencialUnidadeFuncional nao estará vazia.
			//Como o id de AghCaractUnidFuncionais é 'unf_seq e caracteristica', podem vir dois sequenciais,
			//por esse motivo fora usada uma lista
			if(unidadeFuncionalHorarioAprazamento!=null && unidadeFuncionalHorarioAprazamento.getSeq()!=null){
				List<AghUnidadesFuncionais> unidades = getAghuFacade().pesquisarUnidadesFuncionaisPorCodigoDescricaoECaracteristicas(
						unidadeFuncionalHorarioAprazamento.getSeq().toString(), null, null, 
						ConstanteAghCaractUnidFuncionais.UNID_EMERGENCIA,
						ConstanteAghCaractUnidFuncionais.UNID_INTERNACAO,
						ConstanteAghCaractUnidFuncionais.UNID_HOSP_DIA);
				if(unidades == null || unidades.isEmpty()){
					excecoes.add(new ApplicationBusinessException(ManterHorarioInicAprazamentoRNExceptionCode.MPM_01274));
				}
			}*/
		}		
	}
	
	/**
	 * Verifica se a unidade funcional do horário de aprazamento foi preenchida
	 * @param unidadeFuncional
	 * @param excecoes
	 */
	private void verificarObrigadoriedadeUnidadeFuncional(AghUnidadesFuncionais unidadeFuncional, BaseListException excecoes){
		if(unidadeFuncional==null){
			excecoes.add(new ApplicationBusinessException(ManterHorarioInicAprazamentoRNExceptionCode.ERRO_UNIDADE_FUNCIONAL_OBRIGATORIA));
		}
	}	
	
	/**
	 * @ORADB: MPMK_HIA_RN.RN_HIAP_VER_FRQ_APZ
	 * @param tipoFrequenciaAprazamento
	 * @param excecoes
	 */
	private void verificarFrequenciaAprazamento(MpmTipoFrequenciaAprazamento tipoFrequenciaAprazamento, BaseListException excecoes){
		if(tipoFrequenciaAprazamento!=null){
			if(!DominioSituacao.A.equals(tipoFrequenciaAprazamento.getIndSituacao())){
				excecoes.add(new ApplicationBusinessException(ManterHorarioInicAprazamentoRNExceptionCode.MPM_00777));
			}
			if(!Boolean.TRUE.equals(tipoFrequenciaAprazamento.getIndDigitaFrequencia())){
				excecoes.add(new ApplicationBusinessException(ManterHorarioInicAprazamentoRNExceptionCode.MPM_00906));
			}
		}
		
	}
	
	/**
	 * Verifica se o tipo de frequência do aprazamento foi preenchida
	 * @param tipoFrequenciaAprazamento
	 * @param excecoes
	 */
	private void verificarObrigatoriedadeFrequenciaAprazamento(MpmTipoFrequenciaAprazamento tipoFrequenciaAprazamento, BaseListException excecoes){
		if(tipoFrequenciaAprazamento==null){
			excecoes.add(new ApplicationBusinessException(ManterHorarioInicAprazamentoRNExceptionCode.ERRO_TIPO_FREQUENCIA_APRAZAMENTO_OBRIGATORIO));
		}
	}
	
	/**
	 * Verifica a obrigatoriedade do horário de início
	 * @param horarioInicio
	 * @param excecoes
	 */
	private void verificarObrigatoriedadeHorarioInicio(Date horarioInicio, BaseListException excecoes){
		if(horarioInicio==null){
			excecoes.add(new ApplicationBusinessException(ManterHorarioInicAprazamentoRNExceptionCode.ERRO_HORARIO_INICIO_OBRIGATORIO));
		}
	}

	/**
	 * Método que verifica a alteração da unidade funcional, a validação só ocorre mediante
	 * a alteração.
	 * @param horarioAprazamento
	 * @param horarioAprazamentoOriginal
	 * @param excecoes
	 */
	private void verificarAlteracaoUnidadeFuncional(
			MpmHorarioInicAprazamento horarioAprazamento,
			MpmHorarioInicAprazamento horarioAprazamentoOriginal,
			BaseListException excecoes) {
		if(CoreUtil.modificados(horarioAprazamento.getUnidadeFuncional(), horarioAprazamentoOriginal.getUnidadeFuncional())){
			verificarUnidadeFuncionalHorarioAprazamento(horarioAprazamento.getUnidadeFuncional(), excecoes);
		}		
	}

	/**
	 * Método que verifica a alteração do tipo de frequência de aprazamento, a validação só ocorre mediante
	 * a alteração.
	 * @param horarioAprazamento
	 * @param horarioAprazamentoOriginal
	 * @param excecoes
	 */
	private void verificarAlteracaoTipoFrequenciaAprazamento(
			MpmHorarioInicAprazamento horarioAprazamento,
			MpmHorarioInicAprazamento horarioAprazamentoOriginal,
			BaseListException excecoes) {
		if(CoreUtil.modificados(horarioAprazamento.getTipoFreqAprazamento(), horarioAprazamentoOriginal.getTipoFreqAprazamento())){
			verificarFrequenciaAprazamento(horarioAprazamento.getTipoFreqAprazamento(), excecoes);
		}
	}
	
	/**
	 * @ORADB: Trigger MPMT_HIA_ARU
	 * @param horarioAprazamento
	 * @param horarioAprazamentoOriginal
	 */
	private void verificarAlteracoesEmCampos(
			MpmHorarioInicAprazamento horarioAprazamento,
			MpmHorarioInicAprazamento horarioAprazamentoOriginal){
		
		//formata a data para possibilitar a comparação apenas das horas e minutos
		SimpleDateFormat format = new SimpleDateFormat("HH:mm");
		String horarioAtual = format.format(horarioAprazamento.getHorarioInicio());
		String horarioOriginal = format.format(horarioAprazamentoOriginal.getHorarioInicio());
		
		if(!horarioAprazamento.getUnidadeFuncional().getSeq().equals(horarioAprazamentoOriginal.getUnidadeFuncional().getSeq())||
				!horarioAprazamento.getTipoFreqAprazamento().getSeq().equals(horarioAprazamentoOriginal.getTipoFreqAprazamento().getSeq())||
						!horarioAprazamento.getId().getFrequencia().equals(horarioAprazamentoOriginal.getId().getFrequencia())||
								!horarioAprazamento.getServidor().getId().equals(horarioAprazamentoOriginal.getServidor().getId())||
										!horarioAtual.equals(horarioOriginal)||
										!horarioAprazamento.getCriadoEm().equals(horarioAprazamentoOriginal.getCriadoEm())||
										!horarioAprazamento.getIndSituacao().equals(horarioAprazamentoOriginal.getIndSituacao())){			
			inserirHorarioAprazamentJn(horarioAprazamento, DominioOperacoesJournal.UPD);			
		}		
	}
	
	/**
	 * Insere journal para alteração de horário de aprazamento.
	 * @param horarioAprazamento
	 * @param operacao
	 */
	private void inserirHorarioAprazamentJn(MpmHorarioInicAprazamento horarioAprazamento, DominioOperacoesJournal operacao){
		
		if(horarioAprazamento!=null && horarioAprazamento.getServidor()!=null && 
				horarioAprazamento.getServidor().getId()!=null && horarioAprazamento.getServidor().getPessoaFisica()!=null){
			MpmHorarioInicAprazamentJn horarioAprazamentJn = new MpmHorarioInicAprazamentJn();
		    horarioAprazamentJn.setCriadoEm(horarioAprazamento.getCriadoEm());
		    horarioAprazamentJn.setFrequencia(horarioAprazamento.getId().getFrequencia());
		    horarioAprazamentJn.setHorarioInicio(horarioAprazamento.getHorarioInicio());
		    horarioAprazamentJn.setIndSituacao(horarioAprazamento.getIndSituacao());
		    horarioAprazamentJn.setSerMatricula(horarioAprazamento.getServidor().getId().getMatricula());
		    horarioAprazamentJn.setSerVinCodigo(horarioAprazamento.getServidor().getId().getVinCodigo());
		    horarioAprazamentJn.setUnfSeq(horarioAprazamento.getId().getUnfSeq());
		    horarioAprazamentJn.setTfqSeq(horarioAprazamento.getId().getTfqSeq());
		    horarioAprazamentJn.setNomeUsuario(horarioAprazamento.getServidor().getUsuario());
		    horarioAprazamentJn.setOperacao(operacao);
			getMpmHorarioInicAprazamentJnDAO().persistir(horarioAprazamentJn);
		}		
	}
	
	/**
	 * @ORADB: Trigger MPMT_HIA_ARD
	 * @param horarioAprazamento
	 */
	public void inserirHorarioAprazamentJnAposExclusaoHorarioInicAprazamento(MpmHorarioInicAprazamento horarioAprazamento){
		inserirHorarioAprazamentJn(horarioAprazamento, DominioOperacoesJournal.DEL);
	}

	/**
	 * Atualiza o horário de início do aprazamento
	 * @param horarioAprazamento
	 * @throws BaseListException 
	 * @throws ApplicationBusinessException  
	 */
	public void atualizarHorarioInicioAprazamento(
			MpmHorarioInicAprazamento horarioAprazamento) throws BaseListException, ApplicationBusinessException {
		MpmHorarioInicAprazamento horarioAprazamentoOriginal = null;
		MpmHorarioInicAprazamentoDAO horarioInicAprazamentoDAO = getMpmHorarioInicAprazamentoDAO();
		
		horarioInicAprazamentoDAO.desatachar(horarioAprazamento);
		horarioAprazamentoOriginal = horarioInicAprazamentoDAO.obterPorChavePrimaria(horarioAprazamento.getId());
		horarioInicAprazamentoDAO.desatachar(horarioAprazamentoOriginal);
		preAtualizarHorarioInicioAprazamento(horarioAprazamento, horarioAprazamentoOriginal);
		MpmHorarioInicAprazamento horarioAprazamentoAtualizado = horarioInicAprazamentoDAO.atualizar(horarioAprazamento);
		horarioInicAprazamentoDAO.flush();
		this.verificarAlteracoesEmCampos(horarioAprazamentoAtualizado, horarioAprazamentoOriginal);
	}

	/**
	 * Efetua tratamentos de validações pré-atualização do horário de início de aprazamento
	 * @ORADB: Trigger MPMT_HIA_BRU 
	 * @param horarioAprazamento
	 * @param horarioAprazamentoOriginal
	 * @throws BaseListException 
	 * @throws ApplicationBusinessException  
	 */
	private void preAtualizarHorarioInicioAprazamento(
			MpmHorarioInicAprazamento horarioAprazamento,
			MpmHorarioInicAprazamento horarioAprazamentoOriginal) throws BaseListException, ApplicationBusinessException {
		//RN02 e RN03 de MPMT_HIA_BRU
		this.validarHorarioAprazamentoAlteracao(horarioAprazamento, horarioAprazamentoOriginal);
		//RN01 de MPMT_HIA_BRU 
		this.atualizarServidor(horarioAprazamento);	
	}

	/**
	 * Insere o horário de início de aprazamento
	 * @param horarioAprazamento
	 * @throws BaseListException
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	public void inserirHorarioInicioAprazamento(
			MpmHorarioInicAprazamento horarioAprazamento) throws BaseListException, ApplicationBusinessException {
		MpmHorarioInicAprazamentoDAO horarioInicAprazamentoDAO = getMpmHorarioInicAprazamentoDAO();
		
		if(horarioInicAprazamentoDAO.obterPorChavePrimaria(horarioAprazamento.getId())!=null){
			throw new ApplicationBusinessException(ManterHorarioInicAprazamentoRNExceptionCode.ERRO_HORARIO_APRAZAMENTO_JA_EXISTENTE);
		}
		preInserirHorarioInicioAprazamento(horarioAprazamento);
		horarioInicAprazamentoDAO.persistir(horarioAprazamento);
		horarioInicAprazamentoDAO.flush();
	}
	
	
	/**
	 * Efetua tratamentos e validações pré-inserção do horário de início de aprazamento.
	 * @ORADB: Trigger MPMT_HIA_BRI
	 * @throws BaseListException
	 * @throws ApplicationBusinessException  
	 */
	private void preInserirHorarioInicioAprazamento(MpmHorarioInicAprazamento horarioAprazamento) throws BaseListException, ApplicationBusinessException{
		//RN01 de MPMT_HIA_BRI 
		this.atualizarDataCriacao(horarioAprazamento);
		//RN03 e RN04 de MPMT_HIA_BRI
		this.validarHorarioAprazamentoInclusao(horarioAprazamento);
		//RN02 de MPMT_HIA_BRI
		this.atualizarServidor(horarioAprazamento);
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
