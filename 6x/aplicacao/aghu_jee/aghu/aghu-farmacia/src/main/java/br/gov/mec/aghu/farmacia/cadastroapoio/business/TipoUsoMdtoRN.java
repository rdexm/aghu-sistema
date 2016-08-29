package br.gov.mec.aghu.farmacia.cadastroapoio.business;

import java.util.Calendar;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.farmacia.business.exception.FarmaciaExceptionCode;
import br.gov.mec.aghu.farmacia.dao.AfaTipoUsoMdtoDAO;
import br.gov.mec.aghu.farmacia.dao.AfaTipoUsoMdtoJnDAO;
import br.gov.mec.aghu.model.AfaTipoUsoMdto;
import br.gov.mec.aghu.model.AfaTipoUsoMdtoJn;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

/**
 * Regras de negócio da tabela AFA_TIPO_USO_MDTOS
 * 
 * @author lcmoura
 * 
 */
@Stateless
public class TipoUsoMdtoRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(TipoUsoMdtoRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AfaTipoUsoMdtoJnDAO afaTipoUsoMdtoJnDAO;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@Inject
	private AfaTipoUsoMdtoDAO afaTipoUsoMdtoDAO;
	
	@EJB
	private IFarmaciaFacade farmaciaFacade;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6028338250963134762L;

	protected IFarmaciaFacade getFarmaciaFacade() {
		return this.farmaciaFacade;
	}

	/**
	 * @ORADB Trigger AFAT_TUM_BRI</br>
	 * 
	 * Passa a descrição para maiúsculo</br> Seta o usuário logado no
	 * sistema</br> Seta a data de criação</br> Verifica se o grupo está ativo
	 * 
	 * @param tipoUsoMdto
	 * @throws ApplicationBusinessException
	 *             AFA-00169 | Não existe Servidor com este Vínculo e Matrícula
	 * @throws ApplicationBusinessException
	 *             AFA_00170 | O grupo uso medicamentos deve estar ativo
	 * @throws ApplicationBusinessException 
	 */
	public void preInsertTipoUsoMdto(AfaTipoUsoMdto tipoUsoMdto)
			throws ApplicationBusinessException {

		// AFA_00036
		AfaTipoUsoMdto tipoUsoMdtoLoaded = getAfaTipoUsoMdtoDAO()
				.obterPorChavePrimaria(tipoUsoMdto.getSigla());
		if (tipoUsoMdtoLoaded != null) {
			throw new ApplicationBusinessException(
					FarmaciaExceptionCode.AFA_00036);
		}

		// Seta o servidor
		this.setServidor(tipoUsoMdto);

		// Seta a data de criação
		tipoUsoMdto.setCriadoEm(Calendar.getInstance().getTime());

		// Verifica se o grupo está ativo
		if (!tipoUsoMdto.getGrupoUsoMedicamento().getIndSituacao().equals(
				DominioSituacao.A)) {
			throw new ApplicationBusinessException(
					FarmaciaExceptionCode.AFA_00170);
		}
	}

	/**
	 * @ORADB Trigger AFAT_TUM_BRU</br>
	 * 
	 * Passa a descrição para maiúsculo</br> Seta o usuário logado no
	 * sistema</br> Seta a data de criação</br> Verifica se o grupo está
	 * ativo</br> Descrição não pode ser alterada
	 * 
	 * @param tipoUsoMdto
	 * @throws ApplicationBusinessException
	 *             AFA-00169 | Não existe Servidor com este Vínculo e Matrícula
	 * @throws ApplicationBusinessException
	 *             AFA_00170 | O grupo uso medicamentos deve estar ativo
	 * @throws ApplicationBusinessException
	 *             AFA_00171 | A descrição do Tipo de Uso de Medicamentos não
	 *             pode ser alterada
	 * @throws ApplicationBusinessException 
	 */
	public void preUpdateTipoUsoMdto(AfaTipoUsoMdto tipoUsoMdtoOld,
			AfaTipoUsoMdto tipoUsoMdto) throws ApplicationBusinessException {

		// Seta o servidor
		this.setServidor(tipoUsoMdto);

		// Seta a data de criação
		tipoUsoMdto.setCriadoEm(Calendar.getInstance().getTime());

		// Verifica se o grupo está ativo
		if (!tipoUsoMdto.getGrupoUsoMedicamento().getIndSituacao().equals(
				DominioSituacao.A)) {
			throw new ApplicationBusinessException(
					FarmaciaExceptionCode.AFA_00170);
		}

		// valida alteração
		if (!CoreUtil.igual(tipoUsoMdtoOld.getDescricao(), tipoUsoMdto
				.getDescricao())) {
			throw new ApplicationBusinessException(
					FarmaciaExceptionCode.AFA_00171);
		}
	}

	/**
	 * @ORADB Trigger AFAT_TUM_BRD</br>
	 * 
	 * Verificar se o registro a ser deletado está dentro do período de exclusão
	 * conforme parametro de sistema
	 * 
	 * @param tipoUsoMdto
	 * @throws ApplicationBusinessException
	 *             AFA_00173 | Erro na recuperação do parâmetro P_DIAS_PERM_DEL
	 *             na AFAK_TUM_RN.RN_TUMP_VER_DELECAO
	 * @throws ApplicationBusinessException
	 *             AFA_00172 | Não é possível excluir o registro por estar fora
	 *             do período permitido para exclusão
	 */
	public void preDeleteTipoUsoMdto(AfaTipoUsoMdto tipoUsoMdto)
			throws BaseException {
		getFarmaciaFacade().verificarDelecao(tipoUsoMdto.getCriadoEm());
	}

	/**
	 * @ORADB Trigger AFAT_TUM_ARU
	 * 
	 * Cria a journal se foi alterado
	 * 
	 * @param tipoUsoMdtoOld
	 * @param tipoUsoMdto
	 * @return
	 */
	public AfaTipoUsoMdtoJn posUpdateTipoUsoMdto(AfaTipoUsoMdto tipoUsoMdtoOld,
			AfaTipoUsoMdto tipoUsoMdto) {

		if (!CoreUtil.igual(tipoUsoMdtoOld.getSigla(), tipoUsoMdto.getSigla())
				|| !CoreUtil.igual(tipoUsoMdtoOld.getGrupoUsoMedicamento(),
						tipoUsoMdto.getGrupoUsoMedicamento())
				|| !CoreUtil.igual(tipoUsoMdtoOld.getRapServidores(),
						tipoUsoMdto.getRapServidores())
				|| !CoreUtil.igual(tipoUsoMdtoOld.getDescricao(), tipoUsoMdto
						.getDescricao())
				|| !CoreUtil.igual(tipoUsoMdtoOld.getIndAntimicrobiano(),
						tipoUsoMdto.getIndAntimicrobiano())
				|| !CoreUtil.igual(tipoUsoMdtoOld.getIndExigeJustificativa(),
						tipoUsoMdto.getIndExigeJustificativa())
				|| !CoreUtil.igual(tipoUsoMdtoOld.getIndAvaliacao(),
						tipoUsoMdto.getIndAvaliacao())
				|| !CoreUtil.igual(tipoUsoMdtoOld
						.getIndExigeDuracaoSolicitada(), tipoUsoMdto
						.getIndExigeDuracaoSolicitada())
				|| !CoreUtil.igual(tipoUsoMdtoOld.getCriadoEm(), tipoUsoMdto
						.getCriadoEm())
				|| !CoreUtil.igual(tipoUsoMdtoOld.getIndSituacao(), tipoUsoMdto
						.getIndSituacao())
				|| !CoreUtil.igual(tipoUsoMdtoOld.getIndQuimioterapico(),
						tipoUsoMdto.getIndQuimioterapico())
				|| !CoreUtil.igual(tipoUsoMdtoOld.getIndControlado(),
						tipoUsoMdto.getIndControlado())) {

			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
			
			AfaTipoUsoMdtoJn jn = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.UPD, AfaTipoUsoMdtoJn.class, servidorLogado != null ? servidorLogado.getUsuario() : null);
			
			jn.setSigla(tipoUsoMdtoOld.getSigla());
			jn.setGupSeq(tipoUsoMdtoOld.getGrupoUsoMedicamento().getSeq());
			jn.setSerMatricula(tipoUsoMdtoOld.getRapServidores().getId()
					.getMatricula());
			jn.setSerVinCodigo(tipoUsoMdtoOld.getRapServidores().getId()
					.getVinCodigo());
			jn.setDescricao(tipoUsoMdtoOld.getDescricao());
			jn.setIndAntimicrobiano(tipoUsoMdtoOld.getIndAntimicrobiano());
			jn.setIndExigeJustificativa(tipoUsoMdtoOld
					.getIndExigeJustificativa());
			jn.setIndAvaliacao(tipoUsoMdtoOld.getIndAvaliacao());
			jn.setIndExigeDuracaoSolicitada(tipoUsoMdtoOld
					.getIndExigeDuracaoSolicitada());
			jn.setCriadoEm(tipoUsoMdtoOld.getCriadoEm());
			jn.setIndSituacao(tipoUsoMdtoOld.getIndSituacao());
			jn.setIndControlado(tipoUsoMdtoOld.getIndControlado());
			jn.setIndQuimioterapico(tipoUsoMdtoOld.getIndQuimioterapico());

			return insertTipoUsoMdtoJn(jn);
		}

		return null;
	}

	/**
	 * @ORADB Trigger AFAT_TUM_ARD
	 * 
	 * Cria a journal
	 * 
	 * @param tipoUsoMdto
	 * @return
	 */
	public AfaTipoUsoMdtoJn posDeleteTipoUsoMdto(AfaTipoUsoMdto tipoUsoMdto) {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		AfaTipoUsoMdtoJn jn = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.DEL, AfaTipoUsoMdtoJn.class, servidorLogado.getUsuario());
		
		jn.setSigla(tipoUsoMdto.getSigla());
		jn.setGupSeq(tipoUsoMdto.getGrupoUsoMedicamento().getSeq());
		jn.setSerMatricula(tipoUsoMdto.getRapServidores().getId()
				.getMatricula());
		jn.setSerVinCodigo(tipoUsoMdto.getRapServidores().getId()
				.getVinCodigo());
		jn.setDescricao(tipoUsoMdto.getDescricao());
		jn.setIndAntimicrobiano(tipoUsoMdto.getIndAntimicrobiano());
		jn.setIndExigeJustificativa(tipoUsoMdto
				.getIndExigeJustificativa());
		jn.setIndAvaliacao(tipoUsoMdto.getIndAvaliacao());
		jn.setIndExigeDuracaoSolicitada(tipoUsoMdto
				.getIndExigeDuracaoSolicitada());
		jn.setCriadoEm(tipoUsoMdto.getCriadoEm());
		jn.setIndSituacao(tipoUsoMdto.getIndSituacao());
		jn.setIndControlado(tipoUsoMdto.getIndControlado());
		jn.setIndQuimioterapico(tipoUsoMdto.getIndQuimioterapico());

		return insertTipoUsoMdtoJn(jn);
	}

//	/**
//	 * Seta o nome do usuário e data de alteração na journal
//	 * 
//	 * @param tipoUsoMdtoJn
//	 */
//	private void preInsertTipoUsoMdtoJn(AfaTipoUsoMdtoJn tipoUsoMdtoJn) {
//
//		tipoUsoMdtoJn.setNomeUsuario(servidorLogado.getPessoaFisica().getNome());
//		tipoUsoMdtoJn.setDataAlteracao(Calendar.getInstance().getTime());
//	}

	/**
	 * Insere um sinonimo de medicamento da sua respectiva "journal"
	 * 
	 * @param tipoUsoMdtoJn
	 * @return
	 */
	private AfaTipoUsoMdtoJn insertTipoUsoMdtoJn(AfaTipoUsoMdtoJn tipoUsoMdtoJn) {
		getAfaTipoUsoMdtoJnDAO().persistir(tipoUsoMdtoJn);
		getAfaTipoUsoMdtoJnDAO().flush();
		return tipoUsoMdtoJn;
	}

	/**
	 * Seta o usuário logado no sistema
	 * 
	 * @param tipoUsoMdto
	 * @throws ApplicationBusinessException
	 *             AFA-00169 | Não existe Servidor com este Vínculo e Matrícula.
	 * @throws ApplicationBusinessException 
	 */
	private void setServidor(AfaTipoUsoMdto tipoUsoMdto)
			throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		if (servidorLogado == null) {
			throw new ApplicationBusinessException(
					FarmaciaExceptionCode.AFA_00169);
		}
		tipoUsoMdto.setRapServidores(servidorLogado);
	}

	protected AfaTipoUsoMdtoDAO getAfaTipoUsoMdtoDAO() {
		return afaTipoUsoMdtoDAO;
	}

	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}
	
	protected AfaTipoUsoMdtoJnDAO getAfaTipoUsoMdtoJnDAO() {
		return afaTipoUsoMdtoJnDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
		
}
