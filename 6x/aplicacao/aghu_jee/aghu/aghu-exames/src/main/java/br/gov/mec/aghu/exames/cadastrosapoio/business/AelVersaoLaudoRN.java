package br.gov.mec.aghu.exames.cadastrosapoio.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSituacaoVersaoLaudo;
import br.gov.mec.aghu.exames.dao.AelCampoUsoFaturamentoDAO;
import br.gov.mec.aghu.exames.dao.AelParametroCamposLaudoDAO;
import br.gov.mec.aghu.exames.dao.AelVersaoLaudoDAO;
import br.gov.mec.aghu.model.AelCampoLaudo;
import br.gov.mec.aghu.model.AelCampoUsoFaturamento;
import br.gov.mec.aghu.model.AelParametroCamposLaudo;
import br.gov.mec.aghu.model.AelVersaoLaudo;
import br.gov.mec.aghu.model.AelVersaoLaudoId;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.ApplicationBusinessExceptionCode;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class AelVersaoLaudoRN extends BaseBusiness {

	@EJB
	private AelParametroCamposLaudoRN aelParametroCamposLaudoRN;
	
	private static final Log LOG = LogFactory.getLog(AelVersaoLaudoRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelVersaoLaudoDAO aelVersaoLaudoDAO;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@Inject
	private AelCampoUsoFaturamentoDAO aelCampoUsoFaturamentoDAO;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@Inject
	private AelParametroCamposLaudoDAO aelParametroCamposLaudoDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -1244785866978695877L;

	public enum AelVersaoLaudoRNExceptionCode implements BusinessExceptionCode {
		AEL_00913, AEL_00904, AEL_00369, AEL_00773, AEL_00914, AEL_00774, MENSAGEM_EXAME_MATERIAL_SEM_DESENHO_MASCARA;
	}

	/**
	 * Persistir AelVersaoLaudo
	 * 
	 * @param versaoLaudo
	 * @throws BaseException
	 */
	public void persistir(AelVersaoLaudo versaoLaudo) throws BaseException {

		if (versaoLaudo.getId() != null) {
			this.atualizar(versaoLaudo);

		} else {

			// Valores padrão
			versaoLaudo.setSituacao(DominioSituacaoVersaoLaudo.E); // Em construção

			this.inserir(versaoLaudo);
		}
	}

	/*
	 * Métodos para Inserir
	 */

	/**
	 * ORADB TRIGGER AELT_VEL_BRI (INSERT)
	 * 
	 * @param versaoLaudo
	 * @throws BaseException
	 */
	protected void preInserir(AelVersaoLaudo versaoLaudo) throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		versaoLaudo.setCriadoEm(new Date()); // RN1
		versaoLaudo.setServidor(servidorLogado); // RN2

		//RN1
		if (DominioSituacaoVersaoLaudo.A.equals(versaoLaudo.getSituacao()) 
				|| DominioSituacaoVersaoLaudo.E.equals(versaoLaudo.getSituacao())) {
			this.verificarSituacaoDuplicada(versaoLaudo);
		}
		
		// RN2
		if (!DominioSituacaoVersaoLaudo.E.equals(versaoLaudo.getSituacao())) {
			throw new ApplicationBusinessException(AelVersaoLaudoRNExceptionCode.AEL_00913);
		}

	}

	/**
	 * Inserir AelVersaoLaudo
	 * 
	 * @param versaoLaudo
	 * @throws BaseException
	 */
	protected void inserir(AelVersaoLaudo versaoLaudo) throws BaseException {
		this.preInserir(versaoLaudo);
		this.getAelVersaoLaudoDAO().persistir(versaoLaudo);
		this.flush();
	}

	
	
	/*
	 * Métodos para Atualizar
	 */

	/**
	 * ORADB TRIGGER AELT_VEL_BRU (UPDATE)
	 * 
	 * @param original
	 * @param versaoLaudo
	 * @throws BaseException
	 */
	protected void preAtualizar(AelVersaoLaudo original, AelVersaoLaudo versaoLaudo) throws BaseException {

		// RN2 ORADB PROCEDURE AELK_VEL_RN.RN_VELP_VER_UPDATE: Não é permitido modificar a data de criação e servidor
		if (original.getCriadoEm().getTime() != versaoLaudo.getCriadoEm().getTime() || !original.getServidor().equals(versaoLaudo.getServidor())) {
			throw new ApplicationBusinessException(AelVersaoLaudoRNExceptionCode.AEL_00369);
		}

		// RN3 ORADB PROCEDURE AELK_VEL_RN.RN_VELP_VER_NOME: Não é permitido modificar o nome do desenho (Máscara)
		if (original.getNomeDesenho() != null && !original.getNomeDesenho().equals(versaoLaudo.getNomeDesenho())) {
			throw new ApplicationBusinessException(AelVersaoLaudoRNExceptionCode.AEL_00773);
		}

		// RN4 ORADB PROCEDURE AELK_VEL_RN.RN_VELP_VER_SITUACAO: A situação pode variar somente de 'EM CONSTRUÇÃO' para ASSINADO ou de ASSINADO para INATIVO
		if ((DominioSituacaoVersaoLaudo.I.equals(original.getSituacao()) && DominioSituacaoVersaoLaudo.E.equals(versaoLaudo.getSituacao()))
				|| (DominioSituacaoVersaoLaudo.E.equals(original.getSituacao()) && DominioSituacaoVersaoLaudo.I.equals(versaoLaudo.getSituacao()))) {
			throw new ApplicationBusinessException(AelVersaoLaudoRNExceptionCode.AEL_00914);
		}

	}

	/**
	 * Atualizar AelVersaoLaudo
	 */
	public void atualizar(AelVersaoLaudo versaoLaudo) throws BaseException {

		final AelVersaoLaudo original = this.getAelVersaoLaudoDAO().obterOriginal(versaoLaudo);

		this.preAtualizar(original, versaoLaudo);
		this.getAelVersaoLaudoDAO().merge(versaoLaudo);
		this.getAelVersaoLaudoDAO().flush();
		this.posAtualizar(original, versaoLaudo);
	}

	/**
	 * ORADB PROCEDURE AELP_ENFORCE_VEL_RULES (UPDATE)
	 * 
	 * @param versaoLaudo
	 * @throws BaseException
	 */
	protected void posAtualizar(AelVersaoLaudo original, AelVersaoLaudo versaoLaudo) throws BaseException {

		/*
		 * Quando a situação passar de 'EM CONSTRUÇÃO' para ASSINADO, deve verificar se todos os campos utilizados para faturamento constam na máscara (SISCOLO)
		 */
		if (DominioSituacaoVersaoLaudo.E.equals(original.getSituacao()) && DominioSituacaoVersaoLaudo.A.equals(versaoLaudo.getSituacao())) {

			/*
			 * Quando a situação passar de 'EM CONSTRUÇÃO' para ASSINADO, verificar se todos os campos utilizados para faturamento constam na máscara (SISCOLO)
			 */
			this.verificarUsoFaturamento(versaoLaudo); // RN1

			this.atualizacaoSituacao(versaoLaudo); // RN2

		}
		
		if (DominioSituacaoVersaoLaudo.A.equals(versaoLaudo.getSituacao()) 
				|| DominioSituacaoVersaoLaudo.E.equals(versaoLaudo.getSituacao())) {
			this.verificarSituacaoDuplicada(versaoLaudo);
		}
		
	}

	/**
	 * ORADB PROCEDURE AELK_VEL_RN.RN_VELP_VER_USO_FAT
	 * 
	 * @param versaoLaudo
	 * @throws BaseException
	 */
	protected void verificarUsoFaturamento(AelVersaoLaudo versaoLaudo) throws BaseException {

		final String exaSigla = versaoLaudo.getId().getEmaExaSigla();
		final Integer manSeq = versaoLaudo.getId().getEmaManSeq();
		final Integer seqp = versaoLaudo.getId().getSeqp();

		List<AelCampoUsoFaturamento> listaCampoUsoFaturamento = this.getAelCampoUsoFaturamentoDAO().pesquisarCampoUsoFaturamentoPorExameMaterial(exaSigla, manSeq);

		for (AelCampoUsoFaturamento campoUsoFaturamento : listaCampoUsoFaturamento) {

			AelCampoLaudo campoLaudo = campoUsoFaturamento.getCampoLaudo();

			boolean existeParametroCamposLaudoPorExameMaterialDesenhoMascara = getAelParametroCamposLaudoDAO().existeParametroCamposLaudoPorExameMaterialDesenhoMascara(exaSigla,
					manSeq, seqp, campoLaudo.getSeq());

			if (!existeParametroCamposLaudoPorExameMaterialDesenhoMascara) {
				throw new ApplicationBusinessException(AelVersaoLaudoRNExceptionCode.MENSAGEM_EXAME_MATERIAL_SEM_DESENHO_MASCARA, campoLaudo.getNome());
			}

		}

	}

	/**
	 * ORADB PROCEDURE AELK_VEL_RN.RN_VELP_ATU_SITUACAO Quando a situação passar de 'EM CONSTRUÇÃO' para ASSINADO, os registros com situação ATIVO devem ser 'INATIVADOS'
	 * 
	 * @param versaoLaudo
	 * @throws BaseException
	 */
	protected void atualizacaoSituacao(AelVersaoLaudo versaoLaudo) throws BaseException {

		// Pesquisa versões do laudo ativas e do exame material de análise em questão
		List<AelVersaoLaudo> listaVersaoLaudoInativarSituacao = this.getAelVersaoLaudoDAO().pesquisarVersaoLaudoInativarSituacao(versaoLaudo);

		for (AelVersaoLaudo versaoLaudoAtiva : listaVersaoLaudoInativarSituacao) {

			// INATIVA situação
			versaoLaudoAtiva.setSituacao(DominioSituacaoVersaoLaudo.I);

			// Atualiza a versão do laudo antiga
			this.atualizar(versaoLaudoAtiva);

		}
	}

	/**
	 * ORADB PROCEDURE AELK_VEL_RN.RN_VELP_VER_SIT_DUPL
	 */
	public void verificarSituacaoDuplicada(AelVersaoLaudo versaoLaudo) throws BaseException {

		if (this.getAelVersaoLaudoDAO().existeVersaoLaudoVerificarSituacaoDuplicada(versaoLaudo)) {
			throw new ApplicationBusinessException(AelVersaoLaudoRNExceptionCode.AEL_00904);
		}

	}

	/**
	 * Remove todos os parâmetos de campo laudo através de uma versão do laudo
	 */
	protected void removerParametroCamposLaudo(AelVersaoLaudo versaoLaudo) throws BaseException {

		final String exaSigla = versaoLaudo.getId().getEmaExaSigla();
		final Integer manSeq = versaoLaudo.getId().getEmaManSeq();
		final Integer seqp = versaoLaudo.getId().getSeqp();

		List<AelParametroCamposLaudo> listaParametroCamposLaudo = getAelParametroCamposLaudoDAO().pesquisarParametroCamposLaudoPorVersaoLaudo(exaSigla, manSeq, seqp);

		for (AelParametroCamposLaudo parametroCamposLaudo : listaParametroCamposLaudo) {
			this.getAelParametroCamposLaudoRN().remover(parametroCamposLaudo);
			this.getAelParametroCamposLaudoDAO().flush();
		}

	}

	/*
	 * Métodos para Remover
	 */

	/**
	 * ORADB TRIGGER AELT_GTC_BRD (DELETE)
	 * 
	 * @param versaoLaudo
	 * @throws BaseException
	 */
	private void preRemover(AelVersaoLaudo versaoLaudo) throws BaseException {

		// Se a situação for diferente de 'EM CONSTRUÇÃO'
		if (!DominioSituacaoVersaoLaudo.E.equals(versaoLaudo.getSituacao())) {
			throw new ApplicationBusinessException(AelVersaoLaudoRNExceptionCode.AEL_00774);
		}
	}

	/**
	 * Remover AelVersaoLaudo
	 */
	public void remover(AelVersaoLaudoId id) throws BaseException {
		AelVersaoLaudo versaoLaudo  = getAelVersaoLaudoDAO().obterPorChavePrimaria(id);
		
		if (versaoLaudo == null) {
			throw new BaseRuntimeException(ApplicationBusinessExceptionCode.REGISTRO_NULO_EXCLUSAO);
		}
		
		this.preRemover(versaoLaudo);
		this.removerParametroCamposLaudo(versaoLaudo);
		this.getAelVersaoLaudoDAO().remover(versaoLaudo);
	}

	/**
	 * Getters para RNs e DAOs
	 */

	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}

	protected AelVersaoLaudoDAO getAelVersaoLaudoDAO() {
		return aelVersaoLaudoDAO;
	}

	public AelCampoUsoFaturamentoDAO getAelCampoUsoFaturamentoDAO() {
		return aelCampoUsoFaturamentoDAO;
	}

	protected AelParametroCamposLaudoDAO getAelParametroCamposLaudoDAO() {
		return aelParametroCamposLaudoDAO;
	}
	
	public AelParametroCamposLaudoRN getAelParametroCamposLaudoRN() {
		return aelParametroCamposLaudoRN;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
