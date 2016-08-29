package br.gov.mec.aghu.faturamento.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.faturamento.dao.FatVlrItemProcedHospCompsDAO;
import br.gov.mec.aghu.model.FatVlrItemProcedHospComps;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.BaseException;

@Stateless
public class ValorItemProcedHospCompsON extends BaseBusiness {


@EJB
private ValorItemProcedHospCompsRN valorItemProcedHospCompsRN;

private static final Log LOG = LogFactory.getLog(ValorItemProcedHospCompsON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private FatVlrItemProcedHospCompsDAO fatVlrItemProcedHospCompsDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 7285110053335094017L;

	/**
	 * Metodo para persistir um vlrItemProcedHospComps.
	 * Se a operação for um insert, passar o segundo parâmetro nulo.
	 * @param FatVlrItemProcedHospComps
	 */
	public void persistirVlrItemProcedHospComps(FatVlrItemProcedHospComps vlrItemProcedHospComps, DominioOperacoesJournal operacao) throws BaseException {
		
		if(DominioOperacoesJournal.INS.equals(operacao)){
			this.inserirVlrItemProcedHospComps(vlrItemProcedHospComps);
		}else{
			this.atualizarVlrItemProcedHospComps(vlrItemProcedHospComps);
		}
	}

	/**
	 * Metodo para inserir um vlrItemProcedHospComps.
	 * @param FatVlrItemProcedHospComps
	 */
	protected void inserirVlrItemProcedHospComps(FatVlrItemProcedHospComps vlrItemProcedHospComps) throws BaseException {
		ValorItemProcedHospCompsRN valorItemProcedHospCompsRN = this.getValorItemProcedHospCompsRN();
		FatVlrItemProcedHospCompsDAO vlrItemProcedHospCompsDAO = this.getFatVlrItemProcedHospCompsDAO();
		
		valorItemProcedHospCompsRN.executarAntesDeInserirVlrItemProcedHospComps(vlrItemProcedHospComps);
		
		vlrItemProcedHospCompsDAO.persistir(vlrItemProcedHospComps);
		
		valorItemProcedHospCompsRN.executarStatementAposInserirVlrItemProcedHospComps(vlrItemProcedHospComps);
		
		
	}

	/**
	 * Metodo para atualizar um vlrItemProcedHospComps.
	 * @param FatVlrItemProcedHospComps
	 */
	protected void atualizarVlrItemProcedHospComps(FatVlrItemProcedHospComps vlrItemProcedHospComps) throws BaseException {
		ValorItemProcedHospCompsRN valorItemProcedHospCompsRN = this.getValorItemProcedHospCompsRN();
		
		valorItemProcedHospCompsRN.executarAntesDeAtualizarVlrItemProcedHospComps(vlrItemProcedHospComps);
		
		valorItemProcedHospCompsRN.executarStatementAposAtualizarVlrItemProcedHospComps(vlrItemProcedHospComps);
	}

	protected ValorItemProcedHospCompsRN getValorItemProcedHospCompsRN() {
		return valorItemProcedHospCompsRN;
	}

	protected FatVlrItemProcedHospCompsDAO getFatVlrItemProcedHospCompsDAO() {
		return fatVlrItemProcedHospCompsDAO;
	}

}
