package br.gov.mec.aghu.faturamento.business;

import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.faturamento.business.exception.FaturamentoExceptionCode;
import br.gov.mec.aghu.model.FatConvGrupoItemProced;
import br.gov.mec.aghu.model.FatExcCnvGrpItemProc;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * <p>
 * TODO Implementar metodos <br/>
 * Linhas: 161 <br/>
 * Cursores: 0 <br/>
 * Forks de transacao: 0 <br/>
 * Consultas: 6 tabelas <br/>
 * Alteracoes: 0 tabelas <br/>
 * Metodos: 4 <br/>
 * Metodos externos: 0 <br/>
 * </p>
 * <p>
 * ORADB: <code>FATK_CGI_RN</code>
 * </p>
 * @author gandriotti
 *
 */
@Stateless
@SuppressWarnings("PMD.HierarquiaONRNIncorreta")
public class FaturamentoFatkCgiRN  extends AbstractFatDebugLogEnableRN {

private static final Log LOG = LogFactory.getLog(FaturamentoFatkCgiRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}

	private static final long serialVersionUID = -2979226762263138985L;

	public FaturamentoFatkCgiRN() {

		super();
	}
	
	/**
	 * ORADB Procedure FATK_CGI_RN.RN_CGIP_VER_UNTABHOS
	 * 
	 * @param pTabelaGrupo 
	 * @param pTabelaItem 
	 */
	public void rnCgipVerUntabhos(Short pTabelaGrupo, Short pTabelaItem)
		throws ApplicationBusinessException {
		
		//verifica se a tabela hospitalar referenciada pelas duas FKs sao iguais
		if (!pTabelaGrupo.equals(pTabelaItem)) {
			throw new ApplicationBusinessException(FaturamentoExceptionCode.FAT_00401);
		}
	}
	
	/**
	 * <p>
	 * TODO <br/>
	 * Assinatura: ok<br/>
	 * Referencias externas: ok<br/>
	 * Tabelas: ok<br/>
	 * Codigos de erro: ok<br/>
	 * Implementacao: <b>OK</b> <br/>
	 * Linhas: 34 <br/>
	 * Cursores: 0 <br/>
	 * Forks de transacao: 0 <br/>
	 * Consultas: 1 tabelas <br/>
	 * Alteracoes: 0 tabelas <br/>
	 * Metodos externos: 0 <br/>
	 * </p>
	 * <p>
	 * ORADB: <code>RN_CGIC_VER_EXIGENF</code>
	 * </p>
	 * @param pPhiSeq 
	 * @param pIphPhoSeq 
	 * @param pCnvCodigo 
	 * @param pCnvCspSeq 
	 * @param pInternacao 
	 * @return 
	 * @see FatConvGrupoItemProced
	 */
	public boolean rnCgicVerExigenf(Integer pPhiSeq, Short pIphPhoSeq, Integer pIphSeq, Short pCnvCodigo, Byte pCnvCspSeq) {
		FatConvGrupoItemProced fatConvGrupoItemProced = getFatConvGrupoItensProcedDAO().buscarPrimeiraFatConvGrupoItensProced(pPhiSeq, pIphPhoSeq, pIphSeq, pCnvCodigo, pCnvCspSeq);
		return fatConvGrupoItemProced != null && Boolean.TRUE.equals(fatConvGrupoItemProced.getIndExigeNotaFiscal());
	}

	/**
	 * <p>
	 * TODO <br/>
	 * Assinatura: ok<br/>
	 * Referencias externas: ok<br/>
	 * Tabelas: ok<br/>
	 * Codigos de erro: ok<br/>
	 * Implementacao: <b>OK</b><br/>
	 * Linhas: 31 <br/>
	 * Cursores: 0 <br/>
	 * Forks de transacao: 0 <br/>
	 * Consultas: 1 tabelas <br/>
	 * Alteracoes: 0 tabelas <br/>
	 * Metodos externos: 0 <br/>
	 * </p>
	 * <p>
	 * ORADB: <code>RN_EGIC_VER_EXIGENF</code>
	 * </p>
	 * @param pPhiSeq 
	 * @param pIphPhoSeq 
	 * @param pIphSeq 
	 * @param pCnvCodigo 
	 * @param pCnvCspSeq 
	 * @return 
	 *  
	 * @see FatExcCnvGrpItemProc
	 */
	public boolean rnEgicVerExigenf(Integer pPhiSeq, Short pIphPhoSeq, Integer pIphSeq, Short pCnvCodigo, Byte pCnvCspSeq) throws ApplicationBusinessException {
		Short grcSeq = this.buscarVlrShortAghParametro(AghuParametrosEnum.P_TIPO_GRUPO_CONTA_SUS);
		
		// Verifica se procedimento necessita de NF
		FatExcCnvGrpItemProc primeiraFatExcCnvGrpItemProc = getFatExcCnvGrpItemProcDAO().buscarPrimeiraFatExcCnvGrpItemProcProcedimentoNecessitaNF(pPhiSeq, pIphPhoSeq, pIphSeq, pCnvCodigo, pCnvCspSeq, grcSeq);
		return primeiraFatExcCnvGrpItemProc != null && Boolean.TRUE.equals(primeiraFatExcCnvGrpItemProc.getIndExigeNotaFiscal());
	}
}
