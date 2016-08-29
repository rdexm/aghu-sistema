package br.gov.mec.aghu.paciente.prontuarioonline.business;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioOrigemDocsDigitalizados;
import br.gov.mec.aghu.model.AipOrigemDocGEDs;
import br.gov.mec.aghu.model.AipOrigemDocGEDsJn;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.dao.AipOrigemDocGEDsDAO;
import br.gov.mec.aghu.paciente.dao.AipOrigemDocGEDsJnDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ManterOrigemDocGEDsRN extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(ManterOrigemDocGEDsRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AipOrigemDocGEDsDAO aipOrigemDocGEDsDAO;

@Inject
private AipOrigemDocGEDsJnDAO aipOrigemDocGEDsJnDAO;

    private static final long serialVersionUID = -4547051071575833689L;

    public enum ManterOrigemDocGEDsRNExceptionCode implements BusinessExceptionCode {
	ERRO_29173_1
    }

    public void posAtualizar(AipOrigemDocGEDs origemDocGED, RapServidores servidor) {
	AipOrigemDocGEDs original = getAipOrigemDocGEDsDAO().obterOriginal(origemDocGED.getSeq());
	if (modificados(origemDocGED, original)) {
	    inserirOrigemDocGEDsJournal(original, DominioOperacoesJournal.UPD, servidor);
	}
    }

    public void preAtualizar(AipOrigemDocGEDs origemDocGED) throws ApplicationBusinessException {
	validaMapeamentoUnicoOrigemUpdate(origemDocGED);
    }

    public void preInserir(AipOrigemDocGEDs origemDocGED) throws ApplicationBusinessException {
	validaMapeamentoUnicoOrigemInsert(origemDocGED.getOrigem());
    }

    public void validaMapeamentoUnicoOrigemUpdate(AipOrigemDocGEDs origem) throws ApplicationBusinessException {
	AipOrigemDocGEDs original = getAipOrigemDocGEDsDAO().obterOriginal(origem);

	if (original != null && original.getOrigem() != null && origem != null && original.getOrigem().equals(origem.getOrigem())) {
	    return;
	}

	AipOrigemDocGEDs origemDocGED = new AipOrigemDocGEDs();
	origemDocGED.setOrigem(origem.getOrigem());
	long count = getAipOrigemDocGEDsDAO().pesquisarCount(origemDocGED);
	if (count > 0) {
	    throw new ApplicationBusinessException(ManterOrigemDocGEDsRNExceptionCode.ERRO_29173_1, origem.getOrigem().getDescricao());
	}
    }

    public void validaMapeamentoUnicoOrigemInsert(DominioOrigemDocsDigitalizados origem) throws ApplicationBusinessException {
	AipOrigemDocGEDs origemDocGED = new AipOrigemDocGEDs();
	origemDocGED.setOrigem(origem);
	long count = getAipOrigemDocGEDsDAO().pesquisarCount(origemDocGED);
	if (count > 0) {
	    throw new ApplicationBusinessException(ManterOrigemDocGEDsRNExceptionCode.ERRO_29173_1, origem.getDescricao());
	}
    }

    private boolean modificados(AipOrigemDocGEDs origemDocGED, AipOrigemDocGEDs original) {
	return CoreUtil.modificados(origemDocGED.getOrigem(), original.getOrigem())
		|| CoreUtil.modificados(origemDocGED.getIndSituacao(), original.getIndSituacao())
		|| CoreUtil.modificados(origemDocGED.getSeq(), original.getSeq())
		|| CoreUtil.modificados(origemDocGED.getServidor(), original.getServidor())
		|| CoreUtil.modificados(origemDocGED.getCriadoEm(), original.getCriadoEm())
		|| CoreUtil.modificados(origemDocGED.getReferencia(), original.getReferencia());
    }

    private void inserirOrigemDocGEDsJournal(AipOrigemDocGEDs origemDocGED, DominioOperacoesJournal operacao, RapServidores servidor) {

	AipOrigemDocGEDsJn jn = new AipOrigemDocGEDsJn();

	jn.setCriadoEm(origemDocGED.getCriadoEm());
	jn.setIndSituacao(origemDocGED.getIndSituacao());
	jn.setOrigem(origemDocGED.getOrigem());
	jn.setReferencia(origemDocGED.getReferencia());
	jn.setSeq(origemDocGED.getSeq());

	jn.setOperacao(operacao);
	jn.setServidor(servidor);
	jn.setNomeUsuario(servidor.getUsuario());
	getAipOrigemDocGEDsJnDAO().persistir(jn);
    }

    private AipOrigemDocGEDsJnDAO getAipOrigemDocGEDsJnDAO() {
	return aipOrigemDocGEDsJnDAO;
    }

    protected AipOrigemDocGEDsDAO getAipOrigemDocGEDsDAO() {
	return aipOrigemDocGEDsDAO;
    }

}
