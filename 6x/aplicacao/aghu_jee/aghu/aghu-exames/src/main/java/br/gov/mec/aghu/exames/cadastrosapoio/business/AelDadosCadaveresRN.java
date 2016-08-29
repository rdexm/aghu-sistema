package br.gov.mec.aghu.exames.cadastrosapoio.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.dao.AelAtendimentoDiversosDAO;
import br.gov.mec.aghu.exames.dao.AelDadosCadaveresDAO;
import br.gov.mec.aghu.exames.dao.AelDadosCadaveresJnDAO;
import br.gov.mec.aghu.model.AelDadosCadaveres;
import br.gov.mec.aghu.model.AelDadosCadaveresJn;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.ApplicationBusinessExceptionCode;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@Stateless
public class AelDadosCadaveresRN extends BaseBusiness{
	
	private static final Log LOG = LogFactory.getLog(AelDadosCadaveresRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelDadosCadaveresJnDAO aelDadosCadaveresJnDAO;
	
	@Inject
	private AelAtendimentoDiversosDAO aelAtendimentoDiversosDAO;
	
	@Inject
	private AelDadosCadaveresDAO aelDadosCadaveresDAO; 

	private static final long serialVersionUID = 3925694796757944477L;

	enum AelDadosCadaveresRNExceptionCode implements BusinessExceptionCode {
		MSG_CADAVER_REGISTROS_PENDENTES_ATENDIMENTO_DIVERSO
		;
	}

	
	public void persistirAelDadosCadaveres(final AelDadosCadaveres aelDadosCadaveres) throws BaseException{
		if(aelDadosCadaveres != null){
			if(aelDadosCadaveres.getCausaObito() != null){
				aelDadosCadaveres.setCausaObito(aelDadosCadaveres.getCausaObito().toUpperCase());
			}
			
			if(aelDadosCadaveres.getObservacoes() != null){
				aelDadosCadaveres.setObservacoes(aelDadosCadaveres.getObservacoes().toUpperCase());
			}
		}
		
		
		if (aelDadosCadaveres.getSeq() == null) {
			this.inserir(aelDadosCadaveres);
		} else {
			this.atualizar(aelDadosCadaveres);
		}
	}
	
	/**
	 * ORADB TRIGGER AELT_DDV_BRI (INSERT)
	 */
	private void preInserir(final AelDadosCadaveres aelDadosCadaveres) throws BaseException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		aelDadosCadaveres.setCriadoEm(new Date());

		// Substitui aelk_ael_rn.rn_aelp_atu_servidor (:new.ser_matricula,:new.ser_vin_codigo);
		aelDadosCadaveres.setServidor(servidorLogado);
	}

	private void inserir(final AelDadosCadaveres aelDadosCadaveres) throws BaseException{
		this.preInserir(aelDadosCadaveres);
		this.getAelDadosCadaveresDAO().persistir(aelDadosCadaveres);
	}
	
	private void atualizar(final AelDadosCadaveres aelDadosCadaveres) throws BaseException{
		// AELT_DDV_BRU Foi suprimida, pois o único teste feito era para validar tipo raça, que é um dominio, logo, sempre será um valor válido.
		final AelDadosCadaveres original = getAelDadosCadaveresDAO().obterOriginal(aelDadosCadaveres);
		this.getAelDadosCadaveresDAO().merge(aelDadosCadaveres);
		this.posAtualizar(original, aelDadosCadaveres);
	}
	
	/**
	 * ORADB: AELT_DDV_ARU
	 * @throws ApplicationBusinessException 
	 */
	private void posAtualizar(final AelDadosCadaveres original, final AelDadosCadaveres alterado) throws ApplicationBusinessException{
		if(!CoreUtil.igual(original.getNome(), alterado.getNome()) ||
				!CoreUtil.igual(original.getDthrRemocao(), alterado.getDthrRemocao()) || 
				!CoreUtil.igual(original.getDtNascimento(), alterado.getDtNascimento()) ||
				!CoreUtil.igual(original.getRaca(), alterado.getRaca()) ||
				!CoreUtil.igual(original.getGrupoSanguineo(), alterado.getGrupoSanguineo()) ||
				!CoreUtil.igual(original.getRealizadoPor(), alterado.getRealizadoPor()) ||
				!CoreUtil.igual(original.getLidoPor(), alterado.getLidoPor()) ||
				!CoreUtil.igual(original.getObservacoes(), alterado.getObservacoes()) ||
				!CoreUtil.igual(original.getInstituicaoHospitalarProcedencia(), alterado.getInstituicaoHospitalarProcedencia()) ||
				!CoreUtil.igual(original.getInstituicaoHospitalarRetirada(), alterado.getInstituicaoHospitalarRetirada()) ||
				!CoreUtil.igual(original.getConvenioSaudePlano(), alterado.getConvenioSaudePlano()) 
		){
			createJournal(alterado, DominioOperacoesJournal.UPD);
		}
	}
	
	
	public void remover(final Integer seq) throws BaseException{
		final AelDadosCadaveres aelDadosCadaveres = aelDadosCadaveresDAO.obterPorChavePrimaria(seq);
		if (aelDadosCadaveres == null) {
			throw new BaseRuntimeException(ApplicationBusinessExceptionCode.REGISTRO_NULO_EXCLUSAO);
		}
		
		//verifica dependencia atendimento diverso
		antesExcluir(aelDadosCadaveres);
		
		getAelDadosCadaveresDAO().remover(aelDadosCadaveres);
		getAelDadosCadaveresDAO().flush();
		createJournal(aelDadosCadaveres, DominioOperacoesJournal.DEL);
	}
	
	private void antesExcluir(AelDadosCadaveres aelDadosCadaveres) throws ApplicationBusinessException {
		if (!getAelAtendimentoDiversosDAO().pesquisarAtendimentoDiversoPorCadaver(aelDadosCadaveres.getSeq()).isEmpty()) {
			throw new ApplicationBusinessException(AelDadosCadaveresRNExceptionCode.MSG_CADAVER_REGISTROS_PENDENTES_ATENDIMENTO_DIVERSO);
		}
		
	}

	private void createJournal(final AelDadosCadaveres reg, final DominioOperacoesJournal operacao) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		final AelDadosCadaveresJn journal = BaseJournalFactory.getBaseJournal(operacao, AelDadosCadaveresJn.class, servidorLogado.getUsuario());
		
		journal.setCausaObito(reg.getCausaObito());
		journal.setConvenioSaudePlano(reg.getConvenioSaudePlano());
		journal.setCriadoEm(reg.getCriadoEm());
		journal.setDthrRemocao(reg.getDthrRemocao());
		journal.setDtNascimento(reg.getDtNascimento());
		journal.setGrupoSanguineo(reg.getGrupoSanguineo());
		journal.setInstituicaoHospitalarProcedencia(reg.getInstituicaoHospitalarProcedencia());
		journal.setInstituicaoHospitalarRetirada(reg.getInstituicaoHospitalarRetirada());
		journal.setLidoPor(reg.getLidoPor());
		journal.setNome(reg.getNome());
		journal.setObservacoes(reg.getObservacoes());
		journal.setRaca(reg.getRaca());
		journal.setRealizadoPor(reg.getRealizadoPor());
		journal.setSeq(reg.getSeq());
		journal.setServidor(reg.getServidor());
		
		getAelDadosCadaveresJnDAO().persistir(journal);
	}

	private AelDadosCadaveresJnDAO getAelDadosCadaveresJnDAO(){
		return aelDadosCadaveresJnDAO;
	}
	
	private AelDadosCadaveresDAO getAelDadosCadaveresDAO() {
		return aelDadosCadaveresDAO;
	}
	
	private AelAtendimentoDiversosDAO getAelAtendimentoDiversosDAO() {
		return aelAtendimentoDiversosDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
