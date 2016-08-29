package br.gov.mec.aghu.ambulatorio.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.dao.MamTipoItemEvolucaoDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamTipoItemEvolucaoJNDAO;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.CseCategoriaProfissional;
import br.gov.mec.aghu.model.MamTipoItemEvolucao;
import br.gov.mec.aghu.model.MamTipoItemEvolucaoJn;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@Stateless
public class ManterTipoItenEvolucaoRN extends BaseBusiness {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1630870657261467380L;

	private static final Log LOG = LogFactory.getLog(ManterTipoItenEvolucaoRN.class);
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	@Inject
	private MamTipoItemEvolucaoJNDAO mamTipoItemEvolucaoJNDAO;
	@Inject
	private MamTipoItemEvolucaoDAO mamTipoItemEvolucaoDAO;
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	public enum ManterItensEvolucaoDAOExceptionCode implements BusinessExceptionCode {
		MENSAGEM_TIPO_ITEM_EVOLUCAO_JA_POSSUI_QUESTIONARIOS, MENSAGEM_CATEGORIA_PROFISSIONAL_INATIVA, MENSAGEM_TIPO_ITEM_EVOLUCAO_CAMPOS_INALTERAVEIS;
	}

	public boolean validarAlteracaoCamposInalteraveis(MamTipoItemEvolucao item) throws ApplicationBusinessException {
		MamTipoItemEvolucao tipoItemEvolucaoOriginal = this.getMamTipoItemEvolucaoDAO().obterOriginal(item);
		if (CoreUtil.modificados(item.getDescricao(), tipoItemEvolucaoOriginal.getDescricao())){
			throw new ApplicationBusinessException(ManterItensEvolucaoDAOExceptionCode.MENSAGEM_TIPO_ITEM_EVOLUCAO_CAMPOS_INALTERAVEIS);
		}else if (CoreUtil.modificados(item.getSigla(), tipoItemEvolucaoOriginal.getSigla())) {
			throw new ApplicationBusinessException(ManterItensEvolucaoDAOExceptionCode.MENSAGEM_TIPO_ITEM_EVOLUCAO_CAMPOS_INALTERAVEIS);
		}else if (CoreUtil.modificados(item.getOrdem(), tipoItemEvolucaoOriginal.getOrdem())) {
			throw new ApplicationBusinessException(ManterItensEvolucaoDAOExceptionCode.MENSAGEM_TIPO_ITEM_EVOLUCAO_CAMPOS_INALTERAVEIS);
		}
		return true;
	}
	
	public void salvarItenEvolucao(MamTipoItemEvolucao item) throws BaseException, ApplicationBusinessException  {

		MamTipoItemEvolucao tipoItemEvolucaoOriginal = this.getMamTipoItemEvolucaoDAO().obterOriginal(item);
		item.setServidor(getRegistroColaboradorFacade().obterServidorAtivoPorUsuario(obterLoginUsuarioLogado(), new Date()));
		if (item.getSeq() != null) {
			if (verificarPermissaoParaAtualizacaoEAdicionaMsgDeCritica(item) && verificarSeHouveAlteracao(item, tipoItemEvolucaoOriginal) &&
					validarAlteracaoCamposInalteraveis(item)) {
				this.gravarTipoItemEvolucaoJournal(tipoItemEvolucaoOriginal);
				this.atualizarItemEvolucao(item);
			}
		} else {
			if (verificarPermissaoParaCadastroEAdicionaMsgDeCritica(item)) {
				this.gravarTipoItemEvolucao(item);
			}
		}
	}

	public boolean verificarPermissaoParaCadastroEAdicionaMsgDeCritica(MamTipoItemEvolucao itemEvolucao) throws ApplicationBusinessException {
		CseCategoriaProfissional categoriaProfissional = itemEvolucao.getCategoriaProfissional();
		if (!categoriaProfissional.getIndSituacao().isAtivo()) {
			throw new ApplicationBusinessException(ManterItensEvolucaoDAOExceptionCode.MENSAGEM_CATEGORIA_PROFISSIONAL_INATIVA);
		}
		return true;
	}

	public boolean verificarSeHouveAlteracao(MamTipoItemEvolucao itemAlterado, MamTipoItemEvolucao itemOriginal) {

		if (CoreUtil.modificados(itemAlterado.getDescricao(), itemOriginal.getDescricao())
				|| CoreUtil.modificados(itemAlterado.getOrdem(), itemOriginal.getOrdem())
				|| CoreUtil.modificados(itemAlterado.getSigla(), itemOriginal.getSigla())
				|| CoreUtil.modificados(itemAlterado.getObrigatorio(), itemOriginal.getObrigatorio())
				|| CoreUtil.modificados(itemAlterado.getPermiteLivre(), itemOriginal.getPermiteLivre())
				|| CoreUtil.modificados(itemAlterado.getPermiteQuest(), itemOriginal.getPermiteQuest())
				|| CoreUtil.modificados(itemAlterado.getPermiteFigura(), itemOriginal.getPermiteFigura())
				|| CoreUtil.modificados(itemAlterado.getIdentificacao(), itemOriginal.getIdentificacao())
				|| CoreUtil.modificados(itemAlterado.getPermiteNega(), itemOriginal.getPermiteNega())
				|| CoreUtil.modificados(itemAlterado.getNotaAdicional(), itemOriginal.getNotaAdicional())
				|| CoreUtil.modificados(itemAlterado.getResExames(), itemOriginal.getResExames())
				|| CoreUtil.modificados(itemAlterado.getSituacao().isAtivo(), itemOriginal.getSituacao().isAtivo())) {

			return true;
		}
		return false;
	}

	public void gravarTipoItemEvolucaoJournal(MamTipoItemEvolucao itemAlterado) {

		MamTipoItemEvolucao tipoItemOriginal = this.getMamTipoItemEvolucaoDAO().obterOriginal(itemAlterado);
		MamTipoItemEvolucaoJn tipoItemJn = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.UPD, MamTipoItemEvolucaoJn.class, itemAlterado.getServidor().getUsuario());
		tipoItemJn.setSeq(tipoItemOriginal.getSeq());
		tipoItemJn.setOrdem(tipoItemOriginal.getOrdem());
		tipoItemJn.setDescricao(tipoItemOriginal.getDescricao());
		tipoItemJn.setSigla(tipoItemOriginal.getSigla());
		tipoItemJn.setCriadoEm(tipoItemOriginal.getCriadoEm());
		tipoItemJn.setSerMatricula(tipoItemOriginal.getServidor().getId().getMatricula());
		tipoItemJn.setSerVinCodigo(tipoItemOriginal.getServidor().getId().getVinCodigo());
		tipoItemJn.setTextoFormatado(tipoItemOriginal.getTextoFormatado());
		tipoItemJn.setCagSeq(Short.valueOf(String.valueOf(tipoItemOriginal.getCategoriaProfissional().getSeq())));
		tipoItemJn.setIndSituacao(DominioSimNao.getInstance(tipoItemOriginal.getSituacao().isAtivo()).name());
		tipoItemJn.setIndObrigatorio(DominioSimNao.getInstance(tipoItemOriginal.getObrigatorio()).name());
		tipoItemJn.setIndPermiteLivre(DominioSimNao.getInstance(tipoItemOriginal.getPermiteLivre()).name());
		tipoItemJn.setIndPermiteQuest(DominioSimNao.getInstance(tipoItemOriginal.getPermiteQuest()).name());
		tipoItemJn.setIndPermiteFigura(DominioSimNao.getInstance(tipoItemOriginal.getPermiteFigura()).name());
		tipoItemJn.setIndIdentificacao(DominioSimNao.getInstance(tipoItemOriginal.getIdentificacao()).name());
		tipoItemJn.setIndNotaAdicional(DominioSimNao.getInstance(tipoItemOriginal.getNotaAdicional()).name());
		tipoItemJn.setIndResExames(DominioSimNao.getInstance(tipoItemOriginal.getResExames()).name());
		tipoItemJn.setIndCargaCont(itemAlterado.getCargaCont());
		tipoItemJn.setIndRegistroFuga(itemAlterado.getRegistroFuga());
		tipoItemJn.setIndPermiteDiagEnf(itemAlterado.getRegistroFuga());
		tipoItemJn.setSerMatricula(itemAlterado.getServidor().getId().getMatricula());
		tipoItemJn.setSerVinCodigo(itemAlterado.getServidor().getId().getVinCodigo());
		tipoItemJn.setCriadoEm(new Date());
		this.getMamTipoItemEvolucaoJNDAO().persistir(tipoItemJn);
		this.getMamTipoItemEvolucaoJNDAO().flush();
	}

	public boolean verificarPermissaoParaAtualizacaoEAdicionaMsgDeCritica(MamTipoItemEvolucao item) throws ApplicationBusinessException {

		if (getMamTipoItemEvolucaoDAO().retornaQtdQuestionariosComTipoItensEvolucao(item) > 0 && !item.getPermiteQuest()) {
			throw new ApplicationBusinessException(ManterItensEvolucaoDAOExceptionCode.MENSAGEM_TIPO_ITEM_EVOLUCAO_JA_POSSUI_QUESTIONARIOS);
		}
		return true;
	}

	public void gravarTipoItemEvolucao(MamTipoItemEvolucao item) throws BaseException {
		item.setCriadoEm(new Date());
		this.getMamTipoItemEvolucaoDAO().persistir(item);
		this.getMamTipoItemEvolucaoDAO().flush();
	}

	public void atualizarItemEvolucao(MamTipoItemEvolucao item) throws BaseException {
		this.getMamTipoItemEvolucaoDAO().atualizar(item);
		this.getMamTipoItemEvolucaoDAO().flush();
	}

	protected MamTipoItemEvolucaoDAO getMamTipoItemEvolucaoDAO() {
		return mamTipoItemEvolucaoDAO;
	}

	protected MamTipoItemEvolucaoJNDAO getMamTipoItemEvolucaoJNDAO() {
		return mamTipoItemEvolucaoJNDAO;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return registroColaboradorFacade;
	}

}
