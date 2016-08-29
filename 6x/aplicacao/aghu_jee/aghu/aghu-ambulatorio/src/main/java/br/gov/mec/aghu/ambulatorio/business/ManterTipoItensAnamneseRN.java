package br.gov.mec.aghu.ambulatorio.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.dao.MamTipoItemAnamneseJnDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamTipoItemAnamnesesDAO;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.CseCategoriaProfissional;
import br.gov.mec.aghu.model.MamTipoItemAnamneseJn;
import br.gov.mec.aghu.model.MamTipoItemAnamneses;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@Stateless
public class ManterTipoItensAnamneseRN extends BaseBusiness {
	
	private static final long serialVersionUID = 6667230127850971368L;
	
	@Inject
	private MamTipoItemAnamneseJnDAO mamTipoItemAnamneseJnDAO;
	
	@Inject
	private MamTipoItemAnamnesesDAO mamTipoItemAnamnesesDAO;
	
	@EJB
	private ICascaFacade cascaFacade;

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	public enum ManterItensAnamneseDAOExceptionCode implements BusinessExceptionCode {
		MSG_CATEGORIA_PROFISSIONAL_INATIVA, 
		MSG_TIPO_ITEM_ANAMNESE_JA_POSSUI_QUESTIONARIOS;
	}
	
private static final Log LOG = LogFactory.getLog(ManterTipoItensAnamneseRN.class);
	
	@Override
	protected Log getLogger() {
		return LOG;
	}
	
	public List<CseCategoriaProfissional> pesquisarListaCategoriaProfissional(
			Object filtro) {
		return this.cascaFacade.pesquisarListaCseCategoriaProfissional(
				filtro);
	}

	public Long pesquisarListaCategoriaProfissionalCount(Object filtro) {
		return this.cascaFacade
				.pesquisarListaCseCategoriaProfissionalCount(filtro);
	}
	
	public void verificarEPersistirTipoItemAnamnese(MamTipoItemAnamneses tipoItemAnamnese) throws BaseException {
		
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		
		MamTipoItemAnamneses tipoItemAnamneseOriginal = this.getMamTipoItemAnamnesesDAO().obterOriginal(tipoItemAnamnese);
		
			if (tipoItemAnamnese.getSeq() != null) {
				if (verificarPermissaoParaAtualizacaoEAdicionaMsgDeCritica(tipoItemAnamnese)
						&& verificarSeHouveAlteracao(tipoItemAnamnese, tipoItemAnamneseOriginal)) {
					this.gravarTipoItemAnamneseJournal(tipoItemAnamnese, servidorLogado);
					this.atualizarTipoItemAnamnese(tipoItemAnamnese);
				}
			} else {
				if (verificarPermissaoParaCadastroEAdicionaMsgDeCritica(tipoItemAnamnese)) {
					tipoItemAnamnese.setServidor(servidorLogado);
					this.gravarTipoItemAnamnese(tipoItemAnamnese);
				}
			}
	}
	
	public boolean verificarPermissaoParaCadastroEAdicionaMsgDeCritica(MamTipoItemAnamneses tipoItemAnamnese) throws ApplicationBusinessException {
		CseCategoriaProfissional categoriaProfissional = tipoItemAnamnese.getCategoriaProfissional();
		if (!categoriaProfissional.getIndSituacao().isAtivo()) {
			throw new ApplicationBusinessException(ManterItensAnamneseDAOExceptionCode.MSG_CATEGORIA_PROFISSIONAL_INATIVA);
		}
		return true;
	}
	
	public boolean verificarSeHouveAlteracao(MamTipoItemAnamneses tipoItemAnamnesesAlterado, MamTipoItemAnamneses tipoItemAnamneseOriginal) {
		
		if (CoreUtil.modificados(tipoItemAnamnesesAlterado.getDescricao(), tipoItemAnamneseOriginal.getDescricao())
				|| CoreUtil.modificados(tipoItemAnamnesesAlterado.getOrdem(), tipoItemAnamneseOriginal.getOrdem())
				|| CoreUtil.modificados(tipoItemAnamnesesAlterado.getSigla(), tipoItemAnamneseOriginal.getSigla())
				|| CoreUtil.modificados(tipoItemAnamnesesAlterado.getObrigatorio(), tipoItemAnamneseOriginal.getObrigatorio())
				|| CoreUtil.modificados(tipoItemAnamnesesAlterado.getPermiteLivre(), tipoItemAnamneseOriginal.getPermiteLivre())
				|| CoreUtil.modificados(tipoItemAnamnesesAlterado.getPermiteQuest(), tipoItemAnamneseOriginal.getPermiteQuest())
				|| CoreUtil.modificados(tipoItemAnamnesesAlterado.getPermiteFigura(), tipoItemAnamneseOriginal.getPermiteFigura())
				|| CoreUtil.modificados(tipoItemAnamnesesAlterado.getIdentificacao(), tipoItemAnamneseOriginal.getIdentificacao())
				|| CoreUtil.modificados(tipoItemAnamnesesAlterado.getPermiteNega(), tipoItemAnamneseOriginal.getPermiteNega())
				|| CoreUtil.modificados(tipoItemAnamnesesAlterado.getNotaAdicional(), tipoItemAnamneseOriginal.getNotaAdicional())
				|| CoreUtil.modificados(tipoItemAnamnesesAlterado.getResExames(), tipoItemAnamneseOriginal.getResExames())
				|| CoreUtil.modificados(tipoItemAnamnesesAlterado.getSituacao().isAtivo(), tipoItemAnamneseOriginal.getSituacao().isAtivo())) {
			
			return true;
		}
		return false;
	}
	
	public void gravarTipoItemAnamneseJournal(MamTipoItemAnamneses tipoItemAnamnesesAlterado, RapServidores servidorLogado) {
		
		MamTipoItemAnamneses tipoItemAnamnesesOriginal = this.getMamTipoItemAnamnesesDAO().obterOriginal(tipoItemAnamnesesAlterado);
		
		MamTipoItemAnamneseJn tipoItemAnamneseJn = BaseJournalFactory
				.getBaseJournal(DominioOperacoesJournal.UPD, MamTipoItemAnamneseJn.class, servidorLogado.getUsuario());
		
		tipoItemAnamneseJn.setSeq(tipoItemAnamnesesOriginal.getSeq());
		tipoItemAnamneseJn.setOrdem(tipoItemAnamnesesOriginal.getOrdem());
		tipoItemAnamneseJn.setDescricao(tipoItemAnamnesesOriginal.getDescricao());
		tipoItemAnamneseJn.setSigla(tipoItemAnamnesesOriginal.getSigla());
		tipoItemAnamneseJn.setCriadoEm(tipoItemAnamnesesOriginal.getCriadoEm());
		tipoItemAnamneseJn.setSerMatricula(tipoItemAnamnesesOriginal.getServidor().getId().getMatricula());
		tipoItemAnamneseJn.setSerVinCodigo(tipoItemAnamnesesOriginal.getServidor().getId().getVinCodigo());
		tipoItemAnamneseJn.setTextoFormatado(tipoItemAnamnesesOriginal.getTextoFormatado());
		tipoItemAnamneseJn.setCagSeq(tipoItemAnamnesesOriginal.getCategoriaProfissional().getSeq());
		
		tipoItemAnamneseJn.setIndSituacao(DominioSimNao.getInstance(tipoItemAnamnesesOriginal.getSituacao().isAtivo()).name());
		tipoItemAnamneseJn.setIndObrigatorio(DominioSimNao.getInstance(tipoItemAnamnesesOriginal.getObrigatorio()).name());
		tipoItemAnamneseJn.setIndPermiteLivre(DominioSimNao.getInstance(tipoItemAnamnesesOriginal.getPermiteLivre()).name());
		tipoItemAnamneseJn.setIndPermiteQuest(DominioSimNao.getInstance(tipoItemAnamnesesOriginal.getPermiteQuest()).name());
		tipoItemAnamneseJn.setIndPermiteFigura(DominioSimNao.getInstance(tipoItemAnamnesesOriginal.getPermiteFigura()).name());
		tipoItemAnamneseJn.setIndIdentificacao(DominioSimNao.getInstance(tipoItemAnamnesesOriginal.getIdentificacao()).name());
		tipoItemAnamneseJn.setIndPermiteNega(DominioSimNao.getInstance(tipoItemAnamnesesOriginal.getPermiteNega()).name());
		tipoItemAnamneseJn.setIndNotaAdicional(DominioSimNao.getInstance(tipoItemAnamnesesOriginal.getNotaAdicional()).name());
		tipoItemAnamneseJn.setIndResExames(DominioSimNao.getInstance(tipoItemAnamnesesOriginal.getResExames()).name());
		
		tipoItemAnamneseJn.setSerMatricula(servidorLogado.getId().getMatricula());
		tipoItemAnamneseJn.setSerVinCodigo(servidorLogado.getId().getVinCodigo());
		tipoItemAnamneseJn.setCriadoEm(new Date());
		
		this.getMamTipoItemAnamneseJnDAO().persistir(tipoItemAnamneseJn);
		this.getMamTipoItemAnamneseJnDAO().flush();
		
	}
	
	public boolean verificarPermissaoParaAtualizacaoEAdicionaMsgDeCritica(MamTipoItemAnamneses tipoItemAnamnese)
			throws ApplicationBusinessException {
		
		if (getMamTipoItemAnamnesesDAO().retornaQtdQuestionariosComTipoItemAnamnese(tipoItemAnamnese) > 0 && !tipoItemAnamnese.getPermiteQuest()) {
			throw new ApplicationBusinessException(ManterItensAnamneseDAOExceptionCode.MSG_TIPO_ITEM_ANAMNESE_JA_POSSUI_QUESTIONARIOS);
		}
		return true;
	}
	
	public Long retornaQtdTipoItemAnamnesePorCategoriaProfissionalCount(Integer seqCategoriaProfissional) {
		return getMamTipoItemAnamnesesDAO().buscaTipoItemAnamnesePorCategoriaCount(seqCategoriaProfissional);
	}
	
	public void gravarTipoItemAnamnese(MamTipoItemAnamneses itemAnamnese) throws BaseException {
		itemAnamnese.setCriadoEm(new Date());
		this.getMamTipoItemAnamnesesDAO().persistir(itemAnamnese);
		this.getMamTipoItemAnamnesesDAO().flush();
	}
	
	public List<MamTipoItemAnamneses> pesquisarMamTipoItemAnamneses(Integer seqCategoriaProfissional) {
		return this.getMamTipoItemAnamnesesDAO().pesquisarMamTipoItemAnamneses(seqCategoriaProfissional);
	}
	
	public void atualizarTipoItemAnamnese(MamTipoItemAnamneses tipoItemAnamnese) throws BaseException {
		
		this.getMamTipoItemAnamnesesDAO().atualizar(tipoItemAnamnese);
		this.getMamTipoItemAnamnesesDAO().flush();
	}
	
	protected MamTipoItemAnamnesesDAO getMamTipoItemAnamnesesDAO() {
		return mamTipoItemAnamnesesDAO;
	}
	
	protected MamTipoItemAnamneseJnDAO getMamTipoItemAnamneseJnDAO() {
		return mamTipoItemAnamneseJnDAO;
	}
	
}
