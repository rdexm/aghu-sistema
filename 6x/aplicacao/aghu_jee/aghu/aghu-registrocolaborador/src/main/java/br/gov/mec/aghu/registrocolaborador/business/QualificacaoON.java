package br.gov.mec.aghu.registrocolaborador.business;

import java.util.Calendar;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoQualificacao;
import br.gov.mec.aghu.dominio.DominioTipoQualificacao;
import br.gov.mec.aghu.model.RapInstituicaoQualificadora;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapQualificacao;
import br.gov.mec.aghu.model.RapQualificacoesId;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapTipoAtuacao;
import br.gov.mec.aghu.model.RapTipoQualificacao;
import br.gov.mec.aghu.registrocolaborador.cadastrosbasicos.business.ICadastrosBasicosFacade;
import br.gov.mec.aghu.registrocolaborador.dao.RapPessoasFisicasDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * 
 * @author cvagheti
 * 
 */
@SuppressWarnings("PMD.AghuTooManyMethods")
@Stateless
public class QualificacaoON extends BaseBusiness {


@EJB
private QualificacaoRN qualificacaoRN;

private static final Log LOG = LogFactory.getLog(QualificacaoON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private ICadastrosBasicosFacade cadastrosBasicosFacade;

@Inject
private RapPessoasFisicasDAO rapPessoasFisicasDAO;
	/**
	 * 
	 */
	private static final long serialVersionUID = 6077017940553871511L;

	public enum QualificacaoONExceptionCode implements BusinessExceptionCode {
		MENSAGEM_TIPO_ATUACAO_OBRIGATORIO, //
		MENSAGEM_TIPO_QUALIFICACAO_OBRIGATORIO, //
		MENSAGEM_TIPO_QUALIFICACAO_INEXISTENTE, //
		MENSAGEM_TIPO_ATUACAO_INEXISTENTE, //
		MENSAGEM_INSTIT_QUALIFICADORA_OBRIGATORIO, //
		MENSAGEM_INSTIT_QUALIFICADORA_INEXISTENTE, //
		MENSAGEM_PESSOA_FISICA_INEXISTENTE, //
		MENSAGEM_PESSOA_FISICA_OBRIGATORIO, //
		MENSAGEM_DATA_INICIO_OBRIGATORIO, //
		MENSAGEM_CURSO_GRADUACAO_INVALIDO, //
		MENSAGEM_DATA_INICIO_MAIOR_ATUAL, //
		MENSAGEM_DATAFIM_SITUACAO_ANDAMENTO, MENSAGEM_CURSO_GRADUACAO_INATIVO,
		/**
		 * RAP-00122 - Qualificação concluída ou interrompida deve ter data de
		 * fim.
		 */
		MENSAGEM_CONCLUIDO_INTERROMPIDO_DATA_FINAL_OBRIGATORIA, //
		MENSAGEM_GRADUACAO_DEVE_SER_TIPO_ATUACAO_DISCENTE; //

	}

	/**
	 * 
	 * @param id
	 * @return
	 */
	public RapQualificacao obterQualificacao(RapQualificacoesId id) {
		return getQualificacaoRN().obterQualificacao(id);
	}
	
	public RapQualificacao obterQualificacao(RapQualificacoesId id, Enum[] fetchArgsInnerJoin, Enum[] fetchArgsLeftJoin) {
		return getQualificacaoRN().obterQualificacao(id, fetchArgsInnerJoin, fetchArgsLeftJoin);
	}

	/**
	 * Inclui graduação do servidor.
	 * 
	 * @param qualificacao
	 * @throws ApplicationBusinessException
	 */
	
	public void incluirGraduacao(RapQualificacao qualificacao)
			throws ApplicationBusinessException {
		this.atribuiTipoAtuacaoDiscente(qualificacao);

		validaGraduacao(qualificacao);

		incluir(qualificacao);
	}
	
	/**
	 * 
	 * @param qualificacao
	 * @throws ApplicationBusinessException
	 */
	
	public void incluir(RapQualificacao qualificacao)
			throws ApplicationBusinessException {
		if (qualificacao == null) {
			throw new IllegalArgumentException("Parâmetro obrigatório");
		}

		this.valida(qualificacao);

		try {
			getQualificacaoRN().insert(qualificacao);
		} catch (BaseRuntimeException e) {
			throw new ApplicationBusinessException(e.getCode());
		}
	}	
	
	/**
	 * 
	 * @param qualificacao
	 * @throws ApplicationBusinessException
	 */
	
	public void alterar(RapQualificacao qualificacao) throws ApplicationBusinessException {
		if (qualificacao == null) {
			throw new IllegalArgumentException("Parâmetro obrigatório");
		}

		this.valida(qualificacao);

		try {
			getQualificacaoRN().update(qualificacao);
		} catch (BaseRuntimeException e) {
			throw new ApplicationBusinessException(e.getCode());
		}
	}	
	
	/**
	 * Realiza as validações comuns para todos os tipos de qualificação.
	 * 
	 * @param qualificacao
	 * @throws ApplicationBusinessException
	 */
	private void valida(RapQualificacao qualificacao)
			throws ApplicationBusinessException {
		if (qualificacao.getDtInicio() == null) {
			throw new ApplicationBusinessException(
					QualificacaoONExceptionCode.MENSAGEM_DATA_INICIO_OBRIGATORIO);
		}

		if (qualificacao.getDtInicio().after(Calendar.getInstance().getTime())) {
			throw new ApplicationBusinessException(
					QualificacaoONExceptionCode.MENSAGEM_DATA_INICIO_MAIOR_ATUAL);
		}

		if ((qualificacao.getSituacao() == DominioSituacaoQualificacao.C || qualificacao
				.getSituacao() == DominioSituacaoQualificacao.I)
				&& qualificacao.getDtFim() == null) {
			throw new ApplicationBusinessException(
					QualificacaoONExceptionCode.MENSAGEM_CONCLUIDO_INTERROMPIDO_DATA_FINAL_OBRIGATORIA);
		}

		qualificacao.setSemestre(StringUtils.stripToNull(qualificacao
				.getSemestre()));
		qualificacao.setNroRegConselho(StringUtils.stripToNull(qualificacao
				.getNroRegConselho()));

		qualificacao.getId().setPessoaFisica(
				this.validaAssociacao(qualificacao.getId().getPessoaFisica()));

		qualificacao.setTipoQualificacao(this.validaAssociacao(qualificacao
				.getTipoQualificacao()));

		qualificacao.setTipoAtuacao(this.validaAssociacao(qualificacao
				.getTipoAtuacao()));

		qualificacao.setInstituicaoQualificadora(this
				.validaAssociacao(qualificacao.getInstituicaoQualificadora()));
	}	
	
	/**
	 * Valida associação com instituição qualificadora.
	 * 
	 * @param instituicaoQualificadora
	 * @return
	 * @throws ApplicationBusinessException
	 */
	private RapInstituicaoQualificadora validaAssociacao(
			RapInstituicaoQualificadora instituicaoQualificadora)
			throws ApplicationBusinessException {
		if (instituicaoQualificadora == null
				|| instituicaoQualificadora.getCodigo() == null) {
			throw new ApplicationBusinessException(
					QualificacaoONExceptionCode.MENSAGEM_INSTIT_QUALIFICADORA_OBRIGATORIO);
		}
		instituicaoQualificadora = this.getCadastrosBasicosFacade().obterInstituicaoQualificadora(instituicaoQualificadora.getCodigo());
		if (instituicaoQualificadora == null) {
			throw new ApplicationBusinessException(
					QualificacaoONExceptionCode.MENSAGEM_INSTIT_QUALIFICADORA_INEXISTENTE);

		}
		return instituicaoQualificadora;
	}	
	
	/**
	 * Valida a associação com pessoa fisica.
	 * 
	 * @param pessoaFisica
	 * @return
	 * @throws ApplicationBusinessException
	 */
	private RapPessoasFisicas validaAssociacao(RapPessoasFisicas pessoaFisica)
			throws ApplicationBusinessException {
		if (pessoaFisica == null || pessoaFisica.getCodigo() == null) {
			throw new ApplicationBusinessException(
					QualificacaoONExceptionCode.MENSAGEM_PESSOA_FISICA_OBRIGATORIO);
		}

		pessoaFisica = getPessoasFisicasDAO().obterPorChavePrimaria(pessoaFisica.getCodigo());
		if (pessoaFisica == null) {
			throw new ApplicationBusinessException(
					QualificacaoONExceptionCode.MENSAGEM_PESSOA_FISICA_INEXISTENTE);
		}

		return pessoaFisica;
	}	
	
	/**
	 * Altera a graduação do servidor.
	 * 
	 * @param qualificacao
	 * @param servidorLogado 
	 * @throws ApplicationBusinessException
	 */
	public void alterarGraduacao(RapQualificacao qualificacao, RapServidores servidorLogado) throws ApplicationBusinessException {
		validaGraduacao(qualificacao);
		this.alterar(qualificacao);
	}

	/**
	 * Valida informação de graduação.
	 * 
	 * @param qualificacao
	 * @throws ApplicationBusinessException
	 */
	private void validaGraduacao(RapQualificacao qualificacao) throws ApplicationBusinessException {

		qualificacao.setTipoAtuacao(this.validaAssociacao(qualificacao.getTipoAtuacao()));

		// para graduação o tipo de atuacao deve ser discente
		if (!qualificacao.getTipoAtuacao().getIndDiscente()) {
			throw new ApplicationBusinessException(QualificacaoONExceptionCode.MENSAGEM_GRADUACAO_DEVE_SER_TIPO_ATUACAO_DISCENTE);
		}

		qualificacao.setTipoQualificacao(this.validaAssociacao(qualificacao.getTipoQualificacao()));

		// o tipo deve ser CCC ou CSC
		if (qualificacao.getTipoQualificacao().getTipoQualificacao() != DominioTipoQualificacao.CCC
				&& qualificacao.getTipoQualificacao().getTipoQualificacao() != DominioTipoQualificacao.CSC) {
			throw new ApplicationBusinessException(QualificacaoONExceptionCode.MENSAGEM_CURSO_GRADUACAO_INVALIDO);
		}

		// o nivel do curso deve ser terceiro grau
		Short nivelCurso = null;
		if (qualificacao.getTipoQualificacao().getCccNivelCurso() != null) {
			nivelCurso = qualificacao.getTipoQualificacao().getCccNivelCurso();
		}
		if (qualificacao.getTipoQualificacao().getCscNivelCurso() != null) {
			nivelCurso = qualificacao.getTipoQualificacao().getCscNivelCurso();
		}
		if (nivelCurso == null || nivelCurso != (short) 3) {
			throw new ApplicationBusinessException(QualificacaoONExceptionCode.MENSAGEM_CURSO_GRADUACAO_INVALIDO);
		}

		// quando data de fim informada situacao não pode ser Em Andamento
		if (qualificacao.getDtFim() != null
				&& qualificacao.getSituacao().equals(DominioSituacaoQualificacao.E)) {
			throw new ApplicationBusinessException(QualificacaoONExceptionCode.MENSAGEM_DATAFIM_SITUACAO_ANDAMENTO);
		}
	}

	/**
	 * 
	 * @param qualificacao
	 * @throws ApplicationBusinessException
	 */
	
	public void remover(RapQualificacao qualificacao)
			throws ApplicationBusinessException {
		qualificacao = getQualificacaoRN().obterQualificacao(qualificacao.getId());
		getQualificacaoRN().delete(qualificacao);		
	}

	/**
	 * Atribui o tipo de atuação discente para a qualificação.
	 * 
	 * @param qualificacao
	 * @throws ApplicationBusinessException
	 *             se não encontrar tipo de atuação discente
	 */
	private void atribuiTipoAtuacaoDiscente(RapQualificacao qualificacao)
			throws ApplicationBusinessException {
		RapTipoAtuacao tipoAtuacao = this.getCadastrosBasicosFacade().getTipoAtuacaoDiscente();
		qualificacao.setTipoAtuacao(tipoAtuacao);
	}

	/**
	 * Valida associação com tipo de qualificação.
	 * 
	 * @param tipoQualificacao
	 * @return
	 * @throws ApplicationBusinessException
	 */
	private RapTipoQualificacao validaAssociacao(
			RapTipoQualificacao tipoQualificacao) throws ApplicationBusinessException {
		if (tipoQualificacao == null || tipoQualificacao.getCodigo() == null) {
			throw new ApplicationBusinessException(
					QualificacaoONExceptionCode.MENSAGEM_TIPO_QUALIFICACAO_OBRIGATORIO);
		}

		tipoQualificacao = this.getCadastrosBasicosFacade()
				.obterTipoQualificacao(tipoQualificacao.getCodigo());

		if (tipoQualificacao == null) {
			throw new ApplicationBusinessException(
					QualificacaoONExceptionCode.MENSAGEM_TIPO_QUALIFICACAO_INEXISTENTE);
		}

		if (tipoQualificacao != null
				&& tipoQualificacao.getIndSituacao().equals(DominioSituacao.I)) {
			throw new ApplicationBusinessException(
					QualificacaoONExceptionCode.MENSAGEM_CURSO_GRADUACAO_INATIVO);
		}

		return tipoQualificacao;
	}

	/**
	 * Valida associação com tipo de atuação.
	 * 
	 * @param tipoAtuacao
	 * @return
	 * @throws ApplicationBusinessException
	 */
	private RapTipoAtuacao validaAssociacao(RapTipoAtuacao tipoAtuacao)
			throws ApplicationBusinessException {
		if (tipoAtuacao == null) {
			throw new ApplicationBusinessException(QualificacaoONExceptionCode.MENSAGEM_TIPO_ATUACAO_OBRIGATORIO);
		}

		tipoAtuacao = this.getCadastrosBasicosFacade().obterTipoAtuacao(tipoAtuacao.getCodigo());
		
		if (tipoAtuacao == null) {
			throw new ApplicationBusinessException(QualificacaoONExceptionCode.MENSAGEM_TIPO_ATUACAO_INEXISTENTE);
		}
		return tipoAtuacao;
	}

	protected QualificacaoRN getQualificacaoRN() {
		return qualificacaoRN;
	}
	
	protected RapPessoasFisicasDAO getPessoasFisicasDAO() {
		return rapPessoasFisicasDAO;
	}
	
	protected ICadastrosBasicosFacade getCadastrosBasicosFacade() {
		return cadastrosBasicosFacade;
	}
}