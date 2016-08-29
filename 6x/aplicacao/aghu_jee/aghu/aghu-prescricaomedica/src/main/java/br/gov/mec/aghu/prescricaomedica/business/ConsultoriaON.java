package br.gov.mec.aghu.prescricaomedica.business;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioFinalizacao;
import br.gov.mec.aghu.dominio.DominioIndConcluidaSolicitacaoConsultoria;
import br.gov.mec.aghu.dominio.DominioIndPendenteItemPrescricao;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioOrigemSolicitacaoConsultoria;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.MpmPrescricaoMedica;
import br.gov.mec.aghu.model.MpmRespostaConsultoria;
import br.gov.mec.aghu.model.MpmRespostaConsultoriaHist;
import br.gov.mec.aghu.model.MpmSolicitacaoConsultHist;
import br.gov.mec.aghu.model.MpmSolicitacaoConsultoria;
import br.gov.mec.aghu.model.MpmSolicitacaoConsultoriaId;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.dao.MpmRespostaConsultoriaDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmRespostaConsultoriaHistDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmSolicitacaoConsultHistDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmSolicitacaoConsultoriaDAO;
import br.gov.mec.aghu.prescricaomedica.vo.MpmSolicitacaoConsultoriaVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.business.seguranca.Secure;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * Classe que encapsula as regras de negócio referentes a solicitação de
 * consultoria.
 * 
 * Implementa a pattern Business Delegate para o módulo de consultoria.
 * 
 * Ela pode se valer das classes mais granulares de regras de negócio para
 * implementar suas regras para cada caso de uso deste módulo.
 * 
 * @author gmneto
 * 
 */
@Stateless
public class ConsultoriaON extends BaseBusiness {

	@Inject
	private MpmRespostaConsultoriaHistDAO mpmRespostaConsultoriaHistDAO;
	
	@EJB
	private SolicitacaoConsultoriaRN solicitacaoConsultoriaRN;
	
	@EJB
	private VerificarPrescricaoON verificarPrescricaoON;
	
	private static final Log LOG = LogFactory.getLog(ConsultoriaON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@Inject
	private MpmRespostaConsultoriaDAO mpmRespostaConsultoriaDAO;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@Inject
	private MpmSolicitacaoConsultoriaDAO mpmSolicitacaoConsultoriaDAO;
	
	@Inject
	private MpmSolicitacaoConsultHistDAO mpmSolicitacaoConsultHistDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -4190280921617541768L;

	/**
	 * Enumeracao com todos os codigos de mensagens de excecoes negociais de
	 * consultoria.
	 * 
	 * Cada entrada nesta enumeracao deve corresponder a um chave no message
	 * bundle de consultoria.
	 * 
	 * Porém se não houver uma este valor será enviado como mensagem de execeção
	 * sem localização alguma.
	 * 
	 * @author JoseVaranda
	 */
	private enum ConsultoriaONExceptionCode implements BusinessExceptionCode {
		SCN_ATENDIMENTO_NAO_ACEITA_CONSULTORIA, SCN_ESPECIALIDADE_NAO_ACEITA_CONSULTORIA, SCN_ESPECIALIDADE_INATIVA, 
		SCN_ATENDIMENTO_NAO_VIGENTE, SCN_SOLICITACAO_EXCLUIDA, SCN_NENHUM_USUARIO_LOGADO, SCN_PRESCRICAO_NAO_ENCONTRADA, 
		SCN_SOLICITACAO_ESPECIALIDADE_NAO_PENDENTE, ERRO_REMOCAO_SOLICITACAO_CONSULTORIA, NENHUMA_PROPRIEDDE_FOI_ALTERADA

	}
	
	
	/**
	 * Método utilizado quando a consultoria está sendo atualizada de outras operações
	 * @param solicitacaoConsultoria
	 * @throws BaseException
	 */
	public void  verificarRegrasNegocioAtualizacaoConsultoria(MpmSolicitacaoConsultoria solicitacaoConsultoria) throws BaseException{
		this.verificarRegrasNegocioAtualizacaoConsultoria(solicitacaoConsultoria,solicitacaoConsultoria.getPrescricaoMedica());
	}
	
	

	/**
	 * método responsável por fazer a persistência
	 * 
	 * @param solicitacaoConsultoria
	 * @param idAtendimento
	 * @param idPrescricao
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	public MpmSolicitacaoConsultoria persistirSolicitacaoConsultoria(
			MpmSolicitacaoConsultoria solicitacaoConsultoria,
			Integer idAtendimento, Integer idPrescricao) throws BaseException {

		MpmSolicitacaoConsultoria solicitacao = solicitacaoConsultoria;

		if (solicitacaoConsultoria.getId() == null) {

			this.incluirSolicitacaoConsultoria(solicitacaoConsultoria,
					idAtendimento, idPrescricao);

		} else {
			solicitacao = this.atualizarSolicitacaoConsultoria(
					solicitacaoConsultoria, idPrescricao);
		}

		this.getMpmSolicitacaoConsultoriaDAO().flush();

		return solicitacao;
	}

	/**
	 * Método responsável pela persistência de uma solicitação de consultoria.
	 * 
	 * @param solicitacaoConsultoria
	 * @param idAtendimento
	 * @param idPrescricao
	 * @throws ApplicationBusinessException 
	 * @throws ValidacaoSolicitacaoConsultoriaException
	 */
	@Secure
	private void incluirSolicitacaoConsultoria(
			MpmSolicitacaoConsultoria solicitacaoConsultoria,
			Integer idAtendimento, Integer idPrescricao) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		VerificarPrescricaoON verificarPrescricaoON = getVerificarPrescricaoON();

		this.inicializarValoresDefault(solicitacaoConsultoria);

		MpmPrescricaoMedica prescricao = getSolicitacaoConsultoriaRN()
				.obterPrescricaoPorChave(idAtendimento, idPrescricao);
		

		if (prescricao == null) {
			throw new ApplicationBusinessException(
					ConsultoriaONExceptionCode.SCN_PRESCRICAO_NAO_ENCONTRADA);
		}
		
		//Verifica se a prescrição está vigente
		verificarPrescricaoON.verificaAtendimento(prescricao.getAtendimento());
		verificarPrescricaoON.validaDataFimAtendimento(prescricao.getAtendimento(), prescricao.getDthrFim(), prescricao.getDthrInicio());

		this.setarValoresIniciais(solicitacaoConsultoria, prescricao);

		this.validarSolicitacaoConsultoria(solicitacaoConsultoria, prescricao,
				idAtendimento);

		getSolicitacaoConsultoriaRN().verificarExistenciaPrescricao(idAtendimento,
				solicitacaoConsultoria.getDthrSolicitada(),
				solicitacaoConsultoria.getDthrFim(),
				solicitacaoConsultoria.getCriadoEm(), solicitacaoConsultoria
						.getIndPendente(), "I", solicitacaoConsultoria
						.getOrigem());

		solicitacaoConsultoria.setServidorCriacao(servidorLogado); // servidor logado no sistema

		MpmSolicitacaoConsultoriaId mpmSolicitacaoConsultoriasId = new MpmSolicitacaoConsultoriaId();
		mpmSolicitacaoConsultoriasId.setAtdSeq(idAtendimento);
		solicitacaoConsultoria.setId(mpmSolicitacaoConsultoriasId);

		this.gerarValorSequencialId(solicitacaoConsultoria);
		
		solicitacaoConsultoria.setPrescricaoMedica(prescricao);

		this.getMpmSolicitacaoConsultoriaDAO().persistir(solicitacaoConsultoria);

	}
	
	

	/**
	 * Remove uma solicitação de consultoria
	 * @param solicitacaoConsultoria
	 * @throws ApplicationBusinessException
	 */
	public void excluirSolicitacaoConsultoria(MpmSolicitacaoConsultoria solicitacaoConsultoria)
			throws ApplicationBusinessException {
		try {

			solicitacaoConsultoria = this.getMpmSolicitacaoConsultoriaDAO().merge(solicitacaoConsultoria);
			
			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
			
			MpmPrescricaoMedica prescricao = solicitacaoConsultoria.getPrescricaoMedica();
			
			if (DominioIndPendenteItemPrescricao.P.equals(solicitacaoConsultoria.getIndPendente())){
				this.getMpmSolicitacaoConsultoriaDAO().remover(solicitacaoConsultoria);			
				
				if (solicitacaoConsultoria.getSolicitacaoConsultoriaOriginal() != null && DominioIndPendenteItemPrescricao.A.equals(solicitacaoConsultoria.getSolicitacaoConsultoriaOriginal().getIndPendente())) {
					solicitacaoConsultoria.getSolicitacaoConsultoriaOriginal().setIndPendente(DominioIndPendenteItemPrescricao.E);
					this.getMpmSolicitacaoConsultoriaDAO().atualizar(solicitacaoConsultoria.getSolicitacaoConsultoriaOriginal());
				}
			}
			else if (DominioIndPendenteItemPrescricao.N.equals(solicitacaoConsultoria.getIndPendente())){
				if (prescricao.getDthrMovimento() != null && prescricao.getDthrInicio().compareTo(prescricao.getDthrMovimento()) > 0){
					solicitacaoConsultoria.setDthrFim(prescricao.getDthrInicio());
				}
				else{
					solicitacaoConsultoria.setDthrFim(prescricao.getDthrMovimento());			
				}
				solicitacaoConsultoria.setAlteradoEm(new Date());
				solicitacaoConsultoria.setServidorMovimentado(servidorLogado);
				solicitacaoConsultoria.setIndPendente(DominioIndPendenteItemPrescricao.E);
				if (solicitacaoConsultoria.getIndConcluida().equals(DominioIndConcluidaSolicitacaoConsultoria.N)){
					solicitacaoConsultoria.setIndConcluida(DominioIndConcluidaSolicitacaoConsultoria.S);
				}
				getMpmSolicitacaoConsultoriaDAO().atualizar(solicitacaoConsultoria);
			}
			this.getMpmSolicitacaoConsultoriaDAO().flush();
		} catch (Exception e) {
			LOG.error("Erro ao remover solicitação de consultoria.", e);
			throw new ApplicationBusinessException(
					ConsultoriaONExceptionCode.ERRO_REMOCAO_SOLICITACAO_CONSULTORIA);
		}
	}

	/**
	 * Método responsável pela atualização de uma solicitação de consultoria.
	 * 
	 * FIXME Documentar melhor este metodo para deixar claro seu objetivo. No
	 * caso seria atualizar a solicitacao de consultoria atraves de um clone do
	 * registro, o que eh bem diferente de uma atualizacao convencional. Se for
	 * o caso renomear o metodo para deixar isso mais claro.
	 * 
	 * 
	 * @param solicitacaoConsultoria
	 * @param idPrescricao
	 * @return
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	private MpmSolicitacaoConsultoria atualizarSolicitacaoConsultoria(
			MpmSolicitacaoConsultoria solicitacaoConsultoria,
			Integer idPrescricao)
			throws BaseException {
		
		

		MpmSolicitacaoConsultoria solicitacaoRetorno = solicitacaoConsultoria;

		MpmPrescricaoMedica prescricao = getSolicitacaoConsultoriaRN()
				.obterPrescricaoPorChave(solicitacaoConsultoria.getId()
						.getAtdSeq(), idPrescricao);
		
		
		this.verificarRegrasNegocioAtualizacaoConsultoria(solicitacaoConsultoria, prescricao);
		
		// Se a solicitação de consultoria estiver validada ou
		// Se a data de criação da solicitação for anterior a data de
		// movimentação da prescrição.

		if (solicitacaoConsultoria.getIndPendente() == DominioIndPendenteItemPrescricao.N
				|| (solicitacaoConsultoria.getIndPendente() != DominioIndPendenteItemPrescricao.N && solicitacaoConsultoria
						.getCriadoEm().before(prescricao.getDthrMovimento()))) {

			getMpmSolicitacaoConsultoriaDAO().desatachar(solicitacaoConsultoria);

			MpmSolicitacaoConsultoria solicitacaoAnterior = this
					.obterSolicitacaoConsultoriaPorId(solicitacaoConsultoria
							.getId().getAtdSeq(), solicitacaoConsultoria
							.getId().getSeq());

			if (solicitacaoAnterior.getDthrFim() == null) {

				verificarGerarNovaSolicitacao(solicitacaoAnterior,
						solicitacaoConsultoria, idPrescricao,
						solicitacaoConsultoria.getId().getAtdSeq(),
						prescricao); // servidor logado no sistema
				solicitacaoRetorno = solicitacaoAnterior;
			}

			if (solicitacaoAnterior.getDthrFim() != null
					&& (solicitacaoConsultoria.getDthrFim() == null || (solicitacaoAnterior
							.getDthrFim()
							.compareTo(
									solicitacaoConsultoria.getDthrFim()) != 0))) {
				// altera a data de última modificação
				solicitacaoAnterior.setAlteradoEm(new Date());
			}

		}
		else{
			this.getMpmSolicitacaoConsultoriaDAO().atualizar(solicitacaoConsultoria);
		}
		
		this.validarSolicitacaoConsultoria(solicitacaoConsultoria, prescricao,
				solicitacaoConsultoria.getId().getAtdSeq());


		return solicitacaoRetorno;

	}
	
	/**
	 * ORADB MPMT_SCN_BRU - TODO: TERMINAR DE COLOCAR AQUI OS DEMAIS MÉTODOS REFERENTES
	 * À ESTA TRIGGER
	 * @param solicitacaoConsultoria
	 * @param prescricao
	 * @throws BaseException
	 */
	private void verificarRegrasNegocioAtualizacaoConsultoria(
			MpmSolicitacaoConsultoria solicitacaoConsultoria,
			MpmPrescricaoMedica prescricao) throws BaseException {
		VerificarPrescricaoON verificarPrescricaoON = getVerificarPrescricaoON();
		
		//Verifica se a prescrição está vigente
		verificarPrescricaoON.verificaAtendimento(prescricao.getAtendimento());
		verificarPrescricaoON.validaDataFimAtendimento(prescricao.getAtendimento(), prescricao.getDthrFim(), prescricao.getDthrInicio());
		
		
		// FIXME Trocar a operacao textual por uma Enumeration. Avaliar se nao
		// seria melhor fazer direito e criar um metodo para update e outro para
		// insert.
		getSolicitacaoConsultoriaRN().verificarExistenciaPrescricao(
				solicitacaoConsultoria.getId().getAtdSeq(),
				solicitacaoConsultoria.getDthrSolicitada(),
				solicitacaoConsultoria.getDthrFim(),
				solicitacaoConsultoria.getCriadoEm(), solicitacaoConsultoria
						.getIndPendente(), "U", solicitacaoConsultoria
						.getOrigem());

	}

	/**
	 * @param solicitacaoConsultoria
	 * @param idPrescricao
	 * @param idAtendimento
	 * @param matriculaServidorLogado
	 * @param vinculoServidorLogado
	 * @param novoIndicadorPendencia
	 * @param prescricao
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	private void verificarGerarNovaSolicitacao(
			MpmSolicitacaoConsultoria solicitacaoAnterior,
			MpmSolicitacaoConsultoria solicitacaoConsultoria,
			Integer idPrescricao, Integer idAtendimento,
			MpmPrescricaoMedica prescricao)
			throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		VerificarPrescricaoON verificarPrescricaoON = getVerificarPrescricaoON();
		
		boolean gerarNovaSolicitacao = false;

		//Verifica se a prescrição ainda está vigente
		verificarPrescricaoON.verificaAtendimento(prescricao.getAtendimento());
		verificarPrescricaoON.validaDataFimAtendimento(prescricao.getAtendimento(), prescricao.getDthrFim(), prescricao.getDthrInicio());

		// se a solicitação de consultoria estiver com indicador de
		// pendencia não
		// validado
		if (solicitacaoAnterior.getIndPendente() == DominioIndPendenteItemPrescricao.N) {
			
			//Verifica se houve modificação na consultoria
			if (getSolicitacaoConsultoriaRN().hasModificacao(solicitacaoConsultoria)){
				solicitacaoAnterior.setIndPendente(DominioIndPendenteItemPrescricao.A);
				solicitacaoConsultoria.setIndPendente(DominioIndPendenteItemPrescricao.P);
				gerarNovaSolicitacao = true;
				solicitacaoConsultoria.setDthrValida(null);
				// informa a data de dasativação (marcando a solicitação como
				// desativada)
				if (prescricao.getDthrMovimento() != null && prescricao.getDthrInicio().compareTo(prescricao.getDthrMovimento()) > 0){
					solicitacaoAnterior.setDthrFim(prescricao.getDthrInicio());
				}
				else{
					solicitacaoAnterior.setDthrFim(prescricao.getDthrMovimento());			
				}
				solicitacaoAnterior.setServidorMovimentado(servidorLogado);
			}
			else{
				throw new ApplicationBusinessException(ConsultoriaONExceptionCode.NENHUMA_PROPRIEDDE_FOI_ALTERADA);
			}
			
		}
		else{
			solicitacaoAnterior.setMotivo(solicitacaoConsultoria.getMotivo());
			solicitacaoAnterior.setUrgente(solicitacaoConsultoria.isUrgente());
			solicitacaoAnterior.setTipo(solicitacaoConsultoria.getTipo());
		}

		// muda o indicador de conclusão para concluída
		solicitacaoAnterior
				.setIndConcluida(DominioIndConcluidaSolicitacaoConsultoria.S);

		// Se a nova indicador de pendencia for validada, informar o
		// responsável.
		if (solicitacaoConsultoria.getIndPendente() == DominioIndPendenteItemPrescricao.N) {
			solicitacaoAnterior
					.setServidorMovimentado(servidorLogado);
		}

		this.getMpmSolicitacaoConsultoriaDAO().atualizar(solicitacaoAnterior);
		this.getMpmSolicitacaoConsultoriaDAO().flush();
		
		if (gerarNovaSolicitacao){
			this.gerarNovaSolicitacaoConsultoria(solicitacaoAnterior,
					idPrescricao, idAtendimento, solicitacaoConsultoria);
		}
	}
	
	/**
	 * Método que gera nova solicitação de consultoria quando requerido
	 * @param solicitacaoAnterior
	 * @param idPrescricao
	 * @param idAtendimento
	 * @param solicitacaoConsultoria
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	private void gerarNovaSolicitacaoConsultoria(
			MpmSolicitacaoConsultoria solicitacaoAnterior,
			Integer idPrescricao, Integer idAtendimento,
			MpmSolicitacaoConsultoria solicitacaoConsultoria) throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		// faz referência a solicitação de consultoria anterior
		solicitacaoConsultoria
				.setSolicitacaoConsultoriaOriginal(solicitacaoAnterior);
		solicitacaoConsultoria.setServidorCriacao(servidorLogado);
		// reseta o valor do servidor de validação
		solicitacaoConsultoria.setServidorValidacao(null);
		this.incluirSolicitacaoConsultoria(solicitacaoConsultoria,
				idAtendimento, idPrescricao);		
		
	}

	/**
	 * Método que verifica se já existe uma solicitação de consultoria para esta
	 * especialidade em uma mesma prescrição de um mesmo atendimento. TODO
	 * alterar nome de metodo para pesquisarSolicitacaoConsultoria.
	 * 
	 * @param especialidade
	 * @param idAtendimento
	 * @return
	 */
	public MpmSolicitacaoConsultoria verificarSolicitacaoConsultoriaAtivaPorEspecialidade(Short idEspecialidade,
			Integer idAtendimento, Integer idPrescricao) {
		MpmPrescricaoMedica prescricao = getSolicitacaoConsultoriaRN().obterPrescricaoPorChave(idAtendimento, idPrescricao);

		return getMpmSolicitacaoConsultoriaDAO().verificarSolicitacaoConsultoriaAtivaPorEspecialidade(idEspecialidade, idAtendimento,
				idPrescricao, prescricao);
	}

	/**
	 * Método utilizado para obtenção de uma solicitação de consultoria pela
	 * chave primária.
	 * 
	 * @param idAtendimento
	 * @param seqSolicitacaoconsultoria
	 * @return
	 */
	public MpmSolicitacaoConsultoria obterSolicitacaoConsultoriaPorId(
			Integer idAtendimento, Integer seqSolicitacaoconsultoria) {
		MpmSolicitacaoConsultoriaId idConsultoria = new MpmSolicitacaoConsultoriaId(
				idAtendimento, seqSolicitacaoconsultoria);
		return this.getMpmSolicitacaoConsultoriaDAO().obterPorChavePrimaria(idConsultoria,MpmSolicitacaoConsultoria.Fields.ESPECIALIDADE);
	}
	
	/**
	 * Método utilizado para pesquisar as consultorias de uma prescrição médica
	 * @param prescricaoMedica
	 * @return
	 */
	public List<MpmSolicitacaoConsultoria> pesquisarConsultoriasPorPrescricao(MpmPrescricaoMedica prescricaoMedica){
		return this.getMpmSolicitacaoConsultoriaDAO().pesquisarConsultoriasPorPrescricao(prescricaoMedica.getId().getAtdSeq(),
				prescricaoMedica.getId().getSeq());
	}

	/**
	 * Método que determina os valores iniciais padrão na inserção de uma
	 * solicitação de consultoria.
	 *  
	 */
	private void setarValoresIniciais(
			MpmSolicitacaoConsultoria solicitacaoConsultoria,
			MpmPrescricaoMedica prescricao) throws ApplicationBusinessException {

		if (prescricao.getDthrMovimento() != null && prescricao.getDthrInicio().compareTo(prescricao.getDthrMovimento()) > 0){
			solicitacaoConsultoria.setDthrSolicitada(prescricao.getDthrInicio());
		}
		else{
			solicitacaoConsultoria.setDthrSolicitada(prescricao.getDthrMovimento());			
		}


		solicitacaoConsultoria
				.setIndPendente(DominioIndPendenteItemPrescricao.P);
		solicitacaoConsultoria.setOrigem(DominioOrigemSolicitacaoConsultoria.M);
		solicitacaoConsultoria.setCriadoEm(new Date());
		solicitacaoConsultoria
				.setIndConcluida(DominioIndConcluidaSolicitacaoConsultoria.N);

	}

	/**
	 * Método utilizado para inicializar um solicitação de consultoria com os
	 * valores default do banco.
	 * 
	 * @param solicitacaoConsultoria
	 */
	private void inicializarValoresDefault(
			MpmSolicitacaoConsultoria solicitacaoConsultoria) {

		if (solicitacaoConsultoria.getIndImpressao() == null) {
			solicitacaoConsultoria.setIndImpressao(DominioSimNao.N);
		}

		if (solicitacaoConsultoria.getIndSituacao() == null) {
			solicitacaoConsultoria.setIndSituacao(DominioSituacao.A);
		}

		if (solicitacaoConsultoria.getIndUrgencia() == null) {
			solicitacaoConsultoria.setIndUrgencia(DominioSimNao.N);
		}

	}

	/**
	 * Método responsável pelas validações negociais da solicitação de
	 * consultoria
	 * 
	 * @param solicitacaoConsultoria
	 * @throws ValidacaoSolicitacaoConsultoriaException
	 */
	private void validarSolicitacaoConsultoria(
			MpmSolicitacaoConsultoria solicitacaoConsultoria,
			MpmPrescricaoMedica precricao, Integer idAtendimento)
			throws ApplicationBusinessException {

		AghAtendimentos atendimento = this.obterAtendimentoPorId(idAtendimento);

		if (!aceitarConsultoria(atendimento)) {
			throw new ApplicationBusinessException(
					ConsultoriaONExceptionCode.SCN_ATENDIMENTO_NAO_ACEITA_CONSULTORIA);
		}

		// se for uma alteração e a consultoria estiver sendo desativada.
		if (solicitacaoConsultoria.getDthrFim() != null) {

			validarVigenciaAtendimento(atendimento, solicitacaoConsultoria
					.getDthrFim());

			validarVigenciaAtendimento(atendimento, solicitacaoConsultoria
					.getAlteradoEm());

		}

		// se for uma inclusão
		if (solicitacaoConsultoria.getId() == null) {
			validarVigenciaAtendimento(atendimento, solicitacaoConsultoria
					.getDthrSolicitada());

			validarVigenciaAtendimento(atendimento, solicitacaoConsultoria
					.getCriadoEm());
		}

		AghEspecialidades especialidadeSolicitacao = solicitacaoConsultoria
				.getEspecialidade();

		if (especialidadeSolicitacao.getIndConsultoria()
				.equals(DominioSimNao.N)) {

			throw new ApplicationBusinessException(
					ConsultoriaONExceptionCode.SCN_ESPECIALIDADE_NAO_ACEITA_CONSULTORIA);
		}

		if (especialidadeSolicitacao.getIndSituacao().equals(DominioSituacao.I)) {
			throw new ApplicationBusinessException(
					ConsultoriaONExceptionCode.SCN_ESPECIALIDADE_INATIVA);
		}

		if (validarSolitacoesMesmaEspecialidadeNaoPendentes(
				solicitacaoConsultoria, precricao, idAtendimento)) {
			throw new ApplicationBusinessException(
					ConsultoriaONExceptionCode.SCN_SOLICITACAO_ESPECIALIDADE_NAO_PENDENTE,
					precricao.getId().getSeq(), solicitacaoConsultoria
							.getEspecialidade().getNomeEspecialidade());

		}

	}

	/**
	 * verifica se existe outra solicitação para a mesma especialidade nesta
	 * prescrição com indicador de pendencia diferente de "pendente". retorna
	 * true se houver.
	 * 
	 * @param solicitacaoConsultoria
	 * @param precricao
	 */
	private boolean validarSolitacoesMesmaEspecialidadeNaoPendentes(
			MpmSolicitacaoConsultoria solicitacaoConsultoria,
			MpmPrescricaoMedica precricao, Integer idAtendimento) {
		try {
			
			Long result = this.getMpmSolicitacaoConsultoriaDAO().obterSolicitacoesAtivasNaoPendentesPorEspecialidadeCount(
					solicitacaoConsultoria, precricao, idAtendimento);

			return (result != null && result > 0);
			
		} catch (Exception e) {
			LOG.error("Erro ao retornar solicitacoes ativas para especialidade", e);
			return false;
		}
	}

	public List<MpmSolicitacaoConsultoria> pesquisaSolicitacoesConsultoria(Integer atdSeq) {
		return getMpmSolicitacaoConsultoriaDAO().pesquisaSolicitacoesConsultoria(atdSeq);
	}

	public Long pesquisaSolicitacoesConsultoriaCount(Integer atdSeq) {
		Long countBaseAgh  = getMpmSolicitacaoConsultoriaDAO().pesquisaSolicitacoesConsultoriaCount(atdSeq);
		Long countBaseHist = getMpmSolicitacaoConsultHistDAO().obterHistoricoConsultoriaCount(atdSeq);
		return countBaseAgh + countBaseHist;
	}
		
	/**
	 * Verifica se a data de comparação se enquadra na vigência do atendimento.
	 * 
	 * @param atendimento
	 * @param dataComparacao
	 */
	private void validarVigenciaAtendimento(AghAtendimentos atendimento, Date dataComparacao) throws ApplicationBusinessException {
		Date dataIntervaloFinal;
		
		if (atendimento.getDthrFim() != null) {
			dataIntervaloFinal = atendimento.getDthrFim();
		} else {
			dataIntervaloFinal = dataComparacao;
		}
		
		if ( dataComparacao != null && (dataComparacao.before(atendimento.getDthrInicio()) || dataComparacao.after(dataIntervaloFinal))) {
			throw new ApplicationBusinessException(
					ConsultoriaONExceptionCode.SCN_ATENDIMENTO_NAO_VIGENTE);
		}
	}
	
	public String obterRespostasConsultoria(Integer idAtendimento, Integer seqConsultoria, Integer ordem) {
		
		List<MpmRespostaConsultoria> respostas = this.getMpmRespostaConsultoriaDAO().obterRespostasConsultoria(idAtendimento,
				seqConsultoria, ordem);
		SimpleDateFormat dtFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		
		StringBuffer respostaString = new StringBuffer();
		Date criadoEm = null;
		
		if(!respostas.isEmpty()){
			for(MpmRespostaConsultoria resposta : respostas) {
				if(CoreUtil.igual(resposta.getId().getCriadoEm(), criadoEm)) {
					respostaString.append(resposta.getTipoRespostaConsultoria().getDescricao()).append('\n');
					respostaString.append(resposta.getDescricao()).append('\n');
					respostaString.append('\n');
				}
				else {
					respostaString.append("* Resposta em " + dtFormat.format(resposta.getId().getCriadoEm()) + " consultoria " 
							+ resposta.getFinalizacao().getDescricao().toUpperCase() + " por " + resposta.getServidor().getPessoaFisica().getNome()).append('\n');
					respostaString.append(resposta.getTipoRespostaConsultoria().getDescricao()).append('\n');
					respostaString.append(resposta.getDescricao()).append('\n');
					respostaString.append('\n');
					criadoEm = resposta.getId().getCriadoEm();
				}			
			}
		}else{
			List<MpmRespostaConsultoriaHist> respostasHist = this.getMpmRespostaConsultoriaHistDAO().obterRespostasConsultoria(idAtendimento,
					seqConsultoria, ordem);
			for(MpmRespostaConsultoriaHist resposta : respostasHist) {
				if(CoreUtil.igual(resposta.getId().getCriadoEm(), criadoEm)) {
					respostaString.append(resposta.getTipoRespostaConsultoria().getDescricao()).append('\n');
					respostaString.append(resposta.getDescricao()).append('\n');
					respostaString.append('\n');
				}
				else {
					DominioFinalizacao finalizacao = DominioFinalizacao.valueOf(resposta.getIndFinalizacao());
					RapServidores servidor = getIRegistroColaboradorFacade().buscarServidor(resposta.getSerVinCodigo(), resposta.getSerMatricula());
					respostaString.append("* Resposta em " + dtFormat.format(resposta.getId().getCriadoEm()) + " consultoria " 
							+ finalizacao.getDescricao().toUpperCase() + " por " + servidor.getPessoaFisica().getNome()).append('\n');
					respostaString.append(resposta.getTipoRespostaConsultoria().getDescricao()).append('\n');
					respostaString.append(resposta.getDescricao()).append('\n');
					respostaString.append('\n');
					criadoEm = resposta.getId().getCriadoEm();
				}			
			}
		}
		
		return respostaString.toString();
	}
	
	protected MpmRespostaConsultoriaHistDAO getMpmRespostaConsultoriaHistDAO(){
		return mpmRespostaConsultoriaHistDAO;
	}

	/**
	 * Método que determina se uma atendimento aceita consultorias.
	 * 
	 * @param atendimento
	 * @return
	 */
	private boolean aceitarConsultoria(AghAtendimentos atendimento) {
		boolean retorno = false;

		Collection<DominioOrigemAtendimento> origensPermitemConsultoria = EnumSet
				.of(DominioOrigemAtendimento.I, DominioOrigemAtendimento.H,
						DominioOrigemAtendimento.U, DominioOrigemAtendimento.N);

		if (origensPermitemConsultoria.contains(atendimento.getOrigem())) {
			retorno = true;
		}
		return retorno;
	}

	/**
	 * Método usado para obter um atendimento com base na sua chave primária.
	 * 
	 * @param atdSeq
	 * @return
	 */
	private AghAtendimentos obterAtendimentoPorId(int atdSeq) {
		return this.getAghuFacade().obterAghAtendimentoPorChavePrimaria(atdSeq);
	}

	/**
	 * Método responsável pela geração do Sequencial para o Id de uma
	 * solicitação de consultoria.
	 * 
	 * @param solicitacaoConsultoria
	 */
	private void gerarValorSequencialId(
			MpmSolicitacaoConsultoria solicitacaoConsultoria) {
		this.getMpmSolicitacaoConsultoriaDAO().setValorSequencialId(solicitacaoConsultoria);
	}

	protected VerificarPrescricaoON getVerificarPrescricaoON(){
		return verificarPrescricaoON;
	}
	
	
	protected SolicitacaoConsultoriaRN getSolicitacaoConsultoriaRN(){
		return solicitacaoConsultoriaRN;
	}
	
	protected MpmSolicitacaoConsultoriaDAO getMpmSolicitacaoConsultoriaDAO(){
		return mpmSolicitacaoConsultoriaDAO;
	}
	
	protected MpmRespostaConsultoriaDAO getMpmRespostaConsultoriaDAO(){
		return mpmRespostaConsultoriaDAO;
	}
	
	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}



	public List<MpmSolicitacaoConsultoriaVO> pesquisaSolicitacoesConsultoriaVO(
			Integer atdSeq) {
		List<MpmSolicitacaoConsultoriaVO> listaVO = null;
		
		List<MpmSolicitacaoConsultoria> lista = this.getMpmSolicitacaoConsultoriaDAO().obterConsultoriaAtiva(atdSeq);
		if(lista != null && !lista.isEmpty()){
			listaVO = mpmSolicitacaoConsultoriaToVO(lista);
		}else{
			List<MpmSolicitacaoConsultHist> listaHist = getPrescricaoMedicaFacade().obterHistoricoConsultoria(atdSeq);
			listaVO = mpmSolicitacaoConsultoriaHistToVO(listaHist);
		}
		return listaVO;
	}

	private List<MpmSolicitacaoConsultoriaVO> mpmSolicitacaoConsultoriaToVO(
			List<MpmSolicitacaoConsultoria> lista) {
		List<MpmSolicitacaoConsultoriaVO> listaVO = new ArrayList<MpmSolicitacaoConsultoriaVO>();
		
		for(MpmSolicitacaoConsultoria solicConsult : lista){
			MpmSolicitacaoConsultoriaVO vo = new MpmSolicitacaoConsultoriaVO();
			vo.setAtdSeq(solicConsult.getId().getAtdSeq());
			vo.setSeq(solicConsult.getId().getSeq());
			vo.setMotivo(solicConsult.getMotivo());
			vo.setDtHrSolicitada(solicConsult.getDthrSolicitada());
			vo.setNomePessoaServidorCriacao(solicConsult.getServidorCriacao().getPessoaFisica().getNome());
			vo.setNomeReduzidoEspecialidade(solicConsult.getEspecialidade().getNomeReduzido());
			vo.setIndSituacao(solicConsult.getIndSituacao());
			listaVO.add(vo);
		}
		
		return listaVO;
	}
	
	private List<MpmSolicitacaoConsultoriaVO> mpmSolicitacaoConsultoriaHistToVO(
			List<MpmSolicitacaoConsultHist> lista) {
		List<MpmSolicitacaoConsultoriaVO> listaVO = new ArrayList<MpmSolicitacaoConsultoriaVO>();
		
		for(MpmSolicitacaoConsultHist solicConsult : lista){
			MpmSolicitacaoConsultoriaVO vo = new MpmSolicitacaoConsultoriaVO();
			vo.setAtdSeq(solicConsult.getId().getAtdSeq());
			vo.setSeq(solicConsult.getId().getSeq());
			vo.setMotivo(solicConsult.getMotivo());
			vo.setDtHrSolicitada(solicConsult.getDthrSolicitada());
			
			RapServidores servidorValida = getIRegistroColaboradorFacade()
					.obterServidorComPessoaFisicaEVinculoPorVinCodigoMatricula(
							solicConsult.getSerMatriculaValida(),
							solicConsult.getSerVinCodigoValida());
			vo.setNomePessoaServidorCriacao(servidorValida.getPessoaFisica().getNome());
			
			AghEspecialidades espec = getAghuFacade().obterAghEspecialidadesPorChavePrimaria(solicConsult.getEspSeq());
			vo.setNomeReduzidoEspecialidade(espec.getNomeReduzido());
			
			vo.setIndSituacao(solicConsult.getIndSituacao());
			listaVO.add(vo);
		}
		
		return listaVO;
	}
	
	public void atualizarVisualizacaoConsultoria(MpmSolicitacaoConsultoria solicitacaoConsultoria) {
		
		MpmSolicitacaoConsultoria solicitacaoAnterior = this
				.obterSolicitacaoConsultoriaPorId(solicitacaoConsultoria
						.getId().getAtdSeq(), solicitacaoConsultoria
						.getId().getSeq());
		
		if (solicitacaoAnterior != null) {
			solicitacaoAnterior.setDthrConhecimentoResposta(new Date());
			this.getMpmSolicitacaoConsultoriaDAO().atualizar(solicitacaoAnterior);
		}
	}

	protected IPrescricaoMedicaFacade getPrescricaoMedicaFacade(){
		return prescricaoMedicaFacade;
	}
	
	protected IRegistroColaboradorFacade getIRegistroColaboradorFacade(){
		return registroColaboradorFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}



	public MpmSolicitacaoConsultHistDAO getMpmSolicitacaoConsultHistDAO() {
		return mpmSolicitacaoConsultHistDAO;
	}
	
}
