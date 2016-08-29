package br.gov.mec.aghu.business;

import java.util.Date;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.configuracao.dao.AghFeriadosDAO;
import br.gov.mec.aghu.model.AghFeriados;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateValidator;

@Stateless
public class ManterFeriadoRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(ManterFeriadoRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@Inject
	private AghFeriadosDAO aghFeriadosDAO;

	private static final long serialVersionUID = -556845068123271016L;

	public enum ManterFeriadoRNExceptionCode implements BusinessExceptionCode {
		AGH_00566, // Inclusão de feriado somente para datas futuras
		AGH_00567, // Alteração nos feriados somente para datas futuras
		AGH_00568, // Exclusão de feriados somente para datas futuras
		MSG_FERIADO_EXISTENTE, MSG_FERIADO_ERRO_INCLUSAO, MSG_FERIADO_ERRO_ALTERACAO;

	}

	public void remover(Date idExclusao) throws ApplicationBusinessException {
		AghFeriados feriado = this.getAghFeriadosDAO().obterFeriado(idExclusao);
		this.preRemover(feriado);
		this.getAghFeriadosDAO().remover(feriado);
	}

	public void persistirFeriado(AghFeriados feriado, DominioOperacoesJournal operacao) throws BaseException {
		if (operacao.equals(DominioOperacoesJournal.INS)) {
			this.preInserir(feriado);
			this.getAghFeriadosDAO().persistir(feriado);

		} else {
			this.preAtualizar(feriado);
			this.getAghFeriadosDAO().atualizar(feriado);
			this.getAghFeriadosDAO().flush();
		}
	}

	protected void preInserir(AghFeriados feriado) throws ApplicationBusinessException {
		this.verificaDataMenorIgual(feriado, DominioOperacoesJournal.INS);
		this.verificarFeriadoExistente(feriado);
	}

	protected void preAtualizar(AghFeriados feriado) throws ApplicationBusinessException {
		this.verificaDataMenorIgual(feriado, DominioOperacoesJournal.UPD);
	}

	protected void preRemover(AghFeriados feriado) throws ApplicationBusinessException {
		this.verificaDataMenorIgual(feriado, DominioOperacoesJournal.DEL);
	}

	protected void verificaDataMenorIgual(AghFeriados feriado, DominioOperacoesJournal operacao)
			throws ApplicationBusinessException {
		if (DateValidator.validaDataMenorIgual(feriado.getData(), new Date())) {
			if (operacao.equals(DominioOperacoesJournal.INS)) {
				throw new ApplicationBusinessException(ManterFeriadoRNExceptionCode.AGH_00566);
			} else if (operacao.equals(DominioOperacoesJournal.UPD)) {
				throw new ApplicationBusinessException(ManterFeriadoRNExceptionCode.AGH_00567);
			} else if (operacao.equals(DominioOperacoesJournal.DEL)) {
				throw new ApplicationBusinessException(ManterFeriadoRNExceptionCode.AGH_00568);
			}
		}
	}

	private void verificarFeriadoExistente(AghFeriados feriado) throws ApplicationBusinessException {
		AghFeriados feriadoBase = this.getAghFeriadosDAO().obterFeriado(feriado.getData());

		if (feriadoBase != null) {
			throw new ApplicationBusinessException(ManterFeriadoRNExceptionCode.MSG_FERIADO_EXISTENTE);
		}
	}

	protected AghFeriadosDAO getAghFeriadosDAO() {
		return aghFeriadosDAO;
	}

}
