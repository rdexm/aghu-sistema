package br.gov.mec.aghu.emergencia.business;

import java.util.Date;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.emergencia.dao.MamFluxogramaDAO;
import br.gov.mec.aghu.emergencia.dao.MamGravidadeDAO;
import br.gov.mec.aghu.emergencia.dao.MamProtClassifRiscoDAO;
import br.gov.mec.aghu.emergencia.dao.MamProtClassifRiscoJnDAO;
import br.gov.mec.aghu.emergencia.dao.MamTrgGravidadeDAO;
import br.gov.mec.aghu.emergencia.producer.QualificadorUsuario;
import br.gov.mec.aghu.model.MamProtClassifRisco;
import br.gov.mec.aghu.model.MamProtClassifRiscoJn;
import br.gov.mec.aghu.registrocolaborador.vo.Usuario;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

/**
 * Regras de negócio relacionadas à entidade MamProtClassifRisco
 * 
 * @author luismoura
 * 
 */
@Stateless
public class MamProtClassifRiscoRN extends BaseBusiness {
	private static final long serialVersionUID = 1357278607603261830L;

	public enum MamProtClassifRiscoRNExceptionCode implements BusinessExceptionCode {
		MENSAGEM_SUCESSO_CADASTRO_PROTOCOLO_RISCO, //
		MENSAGEM_SUCESSO_ATIVAR_DESATIVAR_PROTOCOLO_RISCO, //
		MENSAGEM_SUCESSO_EXCLUSAO_PROTOCOLO, //
		MENSAGEM_SUCESSO_ALTERACAO_CHECAGEM, //
		MENSAGEM_PROTOCOLO_JA_CADASTRADO, //
		MENSAGEM_ERRO_EXCLUSAO_PROTOCOLO, //
		MENSAGEM_ERRO_EXCLUSAO_PROT_GRAVIDADES, //
		MENSAGEM_ERRO_EXCLUSAO_PROT_ACOLHIMENTO, //
		;
	}

	@Inject
	private MamProtClassifRiscoDAO mamProtClassifRiscoDAO;

	@Inject
	private MamProtClassifRiscoJnDAO mamProtClassifRiscoJnDAO;

	@Inject
	private MamFluxogramaDAO mamFluxogramaDAO;

	@Inject
	private MamGravidadeDAO mamGravidadeDAO;

	@Inject
	private MamTrgGravidadeDAO mamTrgGravidadeDAO;

	@Inject
	@QualificadorUsuario
	private Usuario usuario;

	@Override
	protected Log getLogger() {
		return null;
	}

	/**
	 * Insere um registro de MamProtClassifRisco
	 * 
	 * RN01 de #32283 - manter cadastro de protocolos de classificação de risco
	 * 
	 * @param mamProtClassifRisco
	 * @throws ApplicationBusinessException
	 */
	public void persistir(MamProtClassifRisco mamProtClassifRisco) throws ApplicationBusinessException {

		// 1. Se a consulta C3 retornar resultados apresentar a mensagem MENSAGEM_PROTOCOLO_JA_CADASTRADO e não inserir o registro.
		Boolean exists = mamProtClassifRiscoDAO.existsProtClassifRiscoPorDescricao(mamProtClassifRisco.getDescricao());
		if (exists) {
			throw new ApplicationBusinessException(MamProtClassifRiscoRNExceptionCode.MENSAGEM_PROTOCOLO_JA_CADASTRADO);
		}

		// 2. Seta no campo CRIADO_EM a data atual
		mamProtClassifRisco.setCriadoEm(new Date());

		// 3. Seta nos campos ser_vin_codigo e ser_matricula o código do vínculo e a matrícula do usuário logado no sistema
		mamProtClassifRisco.setSerMatricula(usuario.getMatricula());
		mamProtClassifRisco.setSerVinCodigo(usuario.getVinculo());

		// 4. Realiza insert I1
		mamProtClassifRiscoDAO.persistir(mamProtClassifRisco);
	}

	/**
	 * Ativa/Inativa um registro de MamProtClassifRisco, executando trigger de pos-update
	 * 
	 * RN02 de #32283 - manter cadastro de protocolos de classificação de risco
	 * 
	 * @param mamProtClassifRisco
	 * @throws ApplicationBusinessException
	 */
	public void ativarInativar(MamProtClassifRisco mamProtClassifRisco) throws ApplicationBusinessException {

		MamProtClassifRisco mamProtClassifRiscoOriginal = this.mamProtClassifRiscoDAO.obterOriginal(mamProtClassifRisco);

		// 1. Se ind_situacao = ‘A’, seta ‘I’ e vice-versa
		DominioSituacao situacao = DominioSituacao.A.equals(mamProtClassifRisco.getIndSituacao()) ? DominioSituacao.I : DominioSituacao.A;
		mamProtClassifRisco.setIndSituacao(situacao);

		// 3. Executa RN3 – regra pós-update
		this.posUpdate(mamProtClassifRisco, mamProtClassifRiscoOriginal);

		// 2. Realizar update U1
		mamProtClassifRiscoDAO.atualizar(mamProtClassifRisco);
	}

	/**
	 * Excluir um registro de MamProtClassifRisco, executando trigger de pre e pos-delete
	 * 
	 * RN04 de #32283 - manter cadastro de protocolos de classificação de risco
	 * 
	 * @param mamProtClassifRisco
	 * @throws ApplicationBusinessException
	 */
	public void excluir(MamProtClassifRisco mamProtClassifRisco) throws ApplicationBusinessException {

		mamProtClassifRisco = mamProtClassifRiscoDAO.obterPorChavePrimaria(mamProtClassifRisco.getSeq());

		// 1. Se a consulta C4 retornar resultados apresenta a mensagem MENSAGEM_ERRO_EXCLUSAO_PROTOCOLO
		Boolean exists = mamFluxogramaDAO.existsFluxogramaPorProtocolo(mamProtClassifRisco);
		if (exists) {
			throw new ApplicationBusinessException(MamProtClassifRiscoRNExceptionCode.MENSAGEM_ERRO_EXCLUSAO_PROTOCOLO);
		}

		// 2. Se a consulta C5 retornar resultados apresenta a mensagem MENSAGEM_ERRO_EXCLUSAO_PROT_GRAVIDADES
		exists = mamGravidadeDAO.existsGravidadePorProtocolo(mamProtClassifRisco);
		if (exists) {
			throw new ApplicationBusinessException(MamProtClassifRiscoRNExceptionCode.MENSAGEM_ERRO_EXCLUSAO_PROT_GRAVIDADES);
		}

		// 3. Se a consulta C6 retornar resultados apresenta a mensagem MENSAGEM_ERRO_EXCLUSAO_PROT_ACOLHIMENTO
		exists = mamTrgGravidadeDAO.existsMamTrgGravidadePorProtocolo(mamProtClassifRisco);
		if (exists) {
			throw new ApplicationBusinessException(MamProtClassifRiscoRNExceptionCode.MENSAGEM_ERRO_EXCLUSAO_PROT_ACOLHIMENTO);
		}

		// 4. Executa RN05 – regra pós-delete
		this.posDelete(mamProtClassifRisco);

		// 5. Realiza delete D1
		mamProtClassifRiscoDAO.remover(mamProtClassifRisco);
	}

	/**
	 * Permitir/Bloquear um registro de MamProtClassifRisco, executando trigger de pos-update
	 * 
	 * RN06 de #32283 - manter cadastro de protocolos de classificação de risco
	 * 
	 * @param mamProtClassifRisco
	 * @throws ApplicationBusinessException
	 */
	public void permitirBloquearChecagem(MamProtClassifRisco mamProtClassifRisco) throws ApplicationBusinessException {

		MamProtClassifRisco mamProtClassifRiscoOriginal = this.mamProtClassifRiscoDAO.obterOriginal(mamProtClassifRisco);

		// 1 Se ind_permite_checagem = ‘A’, seta ‘I’ e vice-versa
		DominioSituacao situacao = DominioSituacao.A.equals(mamProtClassifRisco.getIndPermiteChecagem()) ? DominioSituacao.I : DominioSituacao.A;
		mamProtClassifRisco.setIndPermiteChecagem(situacao);

		// 3 Executa RN03 – regra pós-update
		this.posUpdate(mamProtClassifRisco, mamProtClassifRiscoOriginal);

		// 2 Realizar update U2
		mamProtClassifRiscoDAO.atualizar(mamProtClassifRisco);
	}

	/**
	 * Pos-Update de MamProtClassifRisco
	 * 
	 * RN03 de #32283 - manter cadastro de protocolos de classificação de risco
	 * 
	 * @param mamProtClassifRisco
	 */
	private void posUpdate(MamProtClassifRisco mamProtClassifRisco, MamProtClassifRisco mamProtClassifRiscoOriginal) {
		if (CoreUtil.modificados(mamProtClassifRisco.getIndSituacao(), mamProtClassifRiscoOriginal.getIndSituacao())
				|| CoreUtil.modificados(mamProtClassifRisco.getIndPermiteChecagem(), mamProtClassifRiscoOriginal.getIndPermiteChecagem())) {
			this.inserirJournalMamProtClassifRisco(mamProtClassifRiscoOriginal, DominioOperacoesJournal.UPD);
		}

	}

	/**
	 * Pos-Delete de MamProtClassifRisco
	 * 
	 * RN05 de #32283 - manter cadastro de protocolos de classificação de risco
	 * 
	 * @param mamProtClassifRisco
	 */
	private void posDelete(MamProtClassifRisco mamProtClassifRisco) {
		this.inserirJournalMamProtClassifRisco(mamProtClassifRisco, DominioOperacoesJournal.DEL);
	}

	/**
	 * Insere um registro na journal de MamProtClassifRisco
	 * 
	 * @param mamProtClassifRiscoOriginal
	 * @param operacao
	 */
	private void inserirJournalMamProtClassifRisco(MamProtClassifRisco mamProtClassifRiscoOriginal, DominioOperacoesJournal operacao) {
		MamProtClassifRiscoJn mamProtClassifRiscoJn = BaseJournalFactory.getBaseJournal(operacao, MamProtClassifRiscoJn.class, usuario.getLogin());
		mamProtClassifRiscoJn.setCriadoEm(mamProtClassifRiscoOriginal.getCriadoEm());
		mamProtClassifRiscoJn.setDescricao(mamProtClassifRiscoOriginal.getDescricao());
		mamProtClassifRiscoJn.setIndBloqueado(mamProtClassifRiscoOriginal.getIndBloqueado());
		mamProtClassifRiscoJn.setIndPermiteChecagem(mamProtClassifRiscoOriginal.getIndPermiteChecagem());
		mamProtClassifRiscoJn.setIndSituacao(mamProtClassifRiscoOriginal.getIndSituacao());
		mamProtClassifRiscoJn.setSeq(mamProtClassifRiscoOriginal.getSeq());
		mamProtClassifRiscoJn.setSerMatricula(mamProtClassifRiscoOriginal.getSerMatricula());
		mamProtClassifRiscoJn.setSerVinCodigo(mamProtClassifRiscoOriginal.getSerVinCodigo());
		mamProtClassifRiscoJnDAO.persistir(mamProtClassifRiscoJn);
	}
}
