package br.gov.mec.aghu.emergencia.business;

import java.util.Date;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.emergencia.dao.MamOrigemPacienteDAO;
import br.gov.mec.aghu.emergencia.dao.MamOrigemPacienteJnDAO;
import br.gov.mec.aghu.emergencia.producer.QualificadorUsuario;
import br.gov.mec.aghu.model.MamOrigemPaciente;
import br.gov.mec.aghu.model.MamOrigemPacienteJn;
import br.gov.mec.aghu.registrocolaborador.vo.Usuario;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseOptimisticLockException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

/**
 * Regras de negócio relacionadas à entidade MamOrigemPaciente
 * 
 * @author felipe rocha
 * 
 */
@Stateless
public class MamOrigemPacienteRN extends BaseBusiness {
	private static final long serialVersionUID = 1357278607603261830L;

	public enum MamOrigemPacienteRNExceptionCode implements BusinessExceptionCode {
		REGISTRO_NAO_LOCALIZADO_ORIGEM_PACIENTE,
		MENSAGEM_ORIGEM_PACIENTE_INSERIDA_SUCESSO, 
		MENSAGEM_ERRO_ORIGEM_PACIENTE_JA_CADASTRADA, 
		MENSAGEM_SUCESSO_EDICAO_ORIGEM_PACIENTE_SITUACAO,
		MENSAGEM_SUCESSO_EDICAO_ORIGEM_PACIENTE, 
		MENSAGEM_NAO_PERMITIDO_EXCLUIR_ORIGEM_PACIENTE_ACOLHIMENTO_EMERGENCIA,
		MENSAGEM_EXCLUSAO_ORIGEM_PACIENTE_SUCESSO, MENSAGEM_DESCRICAO_OBRIGATORIO,
		OPTIMISTIC_LOCK
	}

	@Inject
	private MamOrigemPacienteDAO mamOrigemPacienteDAO;

	@Inject
	private MamOrigemPacienteJnDAO mamOrigemPacienteJnDAO;

	@Inject
	@QualificadorUsuario
	private Usuario usuario;

	@Override
	protected Log getLogger() {
		return null;
	}

	/**
	 * Insere um registro de MamOrigemPaciente
	 * 
	 * RN01 de #34957 
	 * 
	 * @param  mamOrigemPaciente
	 * @throws ApplicationBusinessException
	 */
	public void persistir(String descricao, DominioSituacao indSituacao) throws ApplicationBusinessException {
		
		if(StringUtils.isEmpty(descricao)){
			throw new ApplicationBusinessException(MamOrigemPacienteRNExceptionCode.MENSAGEM_DESCRICAO_OBRIGATORIO);
			
		}
		// 1. Se a consulta C2 retornar resultados mostrar a mensagem MENSAGEM_ERRO_ORIGEM_PACIENTE_JA_CADASTRADA e não prosseguir a operação.
		Boolean exists = mamOrigemPacienteDAO.isOrigemPacienteJaCadastrado(descricao);
		if (exists) {
			throw new ApplicationBusinessException(MamOrigemPacienteRNExceptionCode.MENSAGEM_ERRO_ORIGEM_PACIENTE_JA_CADASTRADA);
		}

		MamOrigemPaciente mamOrigemPaciente = new MamOrigemPaciente(descricao, indSituacao, new Date(), usuario.getVinculo().intValue(),usuario.getMatricula());
		// 4. Realiza insert I1
		this.mamOrigemPacienteDAO.persistir(mamOrigemPaciente);
		this.mamOrigemPacienteDAO.flush();
	}

	
	/**
	 * Atualiza um registro de MamOrigemPaciente
	 * 
	 * RN02 de #34957 
	 * 
	 * @param  mamOrigemPaciente
	 * @throws ApplicationBusinessException
	 */
	public void alterar(String descricao, DominioSituacao situacao, MamOrigemPaciente origemPaciente) throws ApplicationBusinessException {
		// 3. Seta nos campos ser_vin_codigo e ser_matricula o código do vínculo
		// e a matrícula do usuário logado no sistema
		try {
			origemPaciente.setSerMatriculaAlteracao(usuario.getMatricula());
			origemPaciente.setSerVincCodigoAlteracao(usuario.getVinculo().intValue());
			origemPaciente.setDescricao(descricao);
			origemPaciente.setIndSituacao(situacao);

			origemPaciente.setAlteradoEm(new Date());
			MamOrigemPaciente origemPacienteOriginal = this.mamOrigemPacienteDAO.obterOriginal(origemPaciente);
			
			if(origemPacienteOriginal == null) {
				throw new ApplicationBusinessException(MamOrigemPacienteRNExceptionCode.OPTIMISTIC_LOCK);
			}
			
			// deve atualizar jornal
			boolean atualizaJornal = atualizaJornal(origemPaciente, origemPacienteOriginal);

			// 4. Realiza insert U1
			this.mamOrigemPacienteDAO.atualizar(origemPaciente);
			this.mamOrigemPacienteDAO.flush();

			if (atualizaJornal) {
				this.posUpdate(origemPacienteOriginal);
			}
		} catch (BaseOptimisticLockException e) {
			throw new ApplicationBusinessException(MamOrigemPacienteRNExceptionCode.OPTIMISTIC_LOCK);
		}
	}
	
	
	
	/**
	 * Exclui um registro de MamOrigemPaciente
	 * 
	 * RN05 de #34957 
	 * 
	 * @param  mamOrigemPaciente
	 * @throws ApplicationBusinessException
	 */
	public void excluir(Integer seq) throws ApplicationBusinessException {
		
		MamOrigemPaciente origemPaciente = mamOrigemPacienteDAO.obterPorChavePrimaria(seq);
		
		// 1. Verificar se existe algum acolhimento do paciente vinculado a origem do paciente selecionada, executar a consulta C3.
		Boolean exists = mamOrigemPacienteDAO.isAcolhimentoOrigemPaciente(origemPaciente);
		if (exists) {
			throw new ApplicationBusinessException(MamOrigemPacienteRNExceptionCode.MENSAGEM_NAO_PERMITIDO_EXCLUIR_ORIGEM_PACIENTE_ACOLHIMENTO_EMERGENCIA);
		}
		this.mamOrigemPacienteDAO.remover(origemPaciente);
		posDelete(origemPaciente);
		this.mamOrigemPacienteDAO.flush();
	}
	

	
	/**
	 * Ativa/Inativa um registro de MamOrigemPaciente, executando trigger de pos-update
	 * 
	 * RN06 de #34957 
	 * 
	 * @param mamProtClassifRisco
	 * @throws ApplicationBusinessException
	 */
	public void ativarInativar(MamOrigemPaciente origemPaciente) throws ApplicationBusinessException {
		// 1. Se ind_situacao = ‘A’, seta ‘I’ e vice-versa
		try {
			DominioSituacao situacao = DominioSituacao.A.equals(origemPaciente.getIndSituacao()) ? DominioSituacao.I : DominioSituacao.A;
			origemPaciente.setIndSituacao(situacao);
			origemPaciente.setSerMatriculaAlteracao(usuario.getMatricula());
			origemPaciente.setSerVincCodigoAlteracao(usuario.getVinculo().intValue());

			MamOrigemPaciente origemPacienteOriginal = this.mamOrigemPacienteDAO.obterOriginal(origemPaciente);
			
			if(origemPacienteOriginal == null){
				throw new ApplicationBusinessException(MamOrigemPacienteRNExceptionCode.OPTIMISTIC_LOCK);
			}
			// deve atualizar jornal
			boolean atualizaJornal = atualizaJornal(origemPaciente, origemPacienteOriginal);
			origemPaciente.setAlteradoEm(new Date());
			// 2. Realizar update U1
			mamOrigemPacienteDAO.atualizar(origemPaciente);
			mamOrigemPacienteDAO.flush();
			// 3. Executa RN3 – regra pós-update

			if (atualizaJornal) {
				this.posUpdate(origemPacienteOriginal);
			}
		} catch (BaseOptimisticLockException e) {
			throw new ApplicationBusinessException(MamOrigemPacienteRNExceptionCode.OPTIMISTIC_LOCK);
		}
	}
	
	/**
	 * Pos-Delete de MamOrigemPaciente

	 * @param mamOrigemPaciente
	 */
	private void posDelete(MamOrigemPaciente mamOrigemPaciente) {
			this.inserirJournalMamOrigemPaciente(mamOrigemPaciente, DominioOperacoesJournal.DEL);
	}
	

	/**
	 * Pos-Update de MamOrigemPaciente
	 * 
	 * RN03 de #32283 
	 * 
	 * @param mamOrigemPaciente
	 */
	private void posUpdate(MamOrigemPaciente mamOrigemPaciente) {
			this.inserirJournalMamOrigemPaciente(mamOrigemPaciente, DominioOperacoesJournal.UPD);
	}
	
	private boolean atualizaJornal(MamOrigemPaciente mamOrigemPaciente, MamOrigemPaciente mamOrigemPacienteOriginal){
		if (CoreUtil.modificados(mamOrigemPaciente.getIndSituacao(), mamOrigemPacienteOriginal.getIndSituacao())
				|| CoreUtil.modificados(mamOrigemPaciente.getDescricao(), mamOrigemPacienteOriginal.getDescricao())){
			return true;
		}
		return false;
	}

	
	/**
	 * Insere um registro na journal de MamOrigemPaciente
	 * 
	 * @param mamOrigemPaciente
	 * @param operacao
	 */
	private void inserirJournalMamOrigemPaciente(MamOrigemPaciente mamOrigemPacienteOriginal, DominioOperacoesJournal operacao) {
		MamOrigemPacienteJn mamOrigemPacienteJn = BaseJournalFactory.getBaseJournal(operacao, MamOrigemPacienteJn.class,  usuario.getLogin());
		mamOrigemPacienteJn.setCriadoEm(mamOrigemPacienteOriginal.getCriadoEm());
		mamOrigemPacienteJn.setAlteradoEm(mamOrigemPacienteOriginal.getAlteradoEm());
		mamOrigemPacienteJn.setDescricao(mamOrigemPacienteOriginal.getDescricao());
		mamOrigemPacienteJn.setIndSituacao(mamOrigemPacienteOriginal.getIndSituacao());
		mamOrigemPacienteJn.setSerMatriculaInclusao(mamOrigemPacienteOriginal.getSerMatriculaInclusao());
		mamOrigemPacienteJn.setSerMatriculaAlteracao(mamOrigemPacienteOriginal.getSerMatriculaAlteracao());
		mamOrigemPacienteJn.setSerVincCodigoAlteracao(mamOrigemPacienteOriginal.getSerVincCodigoAlteracao());
		mamOrigemPacienteJn.setSerVincCodigoInclusao(mamOrigemPacienteOriginal.getSerVincCodigoInclusao());
		mamOrigemPacienteJn.setSeq(mamOrigemPacienteOriginal.getSeq());
		mamOrigemPacienteJnDAO.persistir(mamOrigemPacienteJn);
		this.mamOrigemPacienteJnDAO.flush();
	}
	
}