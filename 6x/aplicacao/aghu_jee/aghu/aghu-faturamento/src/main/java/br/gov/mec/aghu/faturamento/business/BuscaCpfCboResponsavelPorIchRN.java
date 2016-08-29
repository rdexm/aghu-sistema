package br.gov.mec.aghu.faturamento.business;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.faturamento.dao.FatItemContaHospitalarDAO;
import br.gov.mec.aghu.faturamento.dao.VFatAssociacaoProcedimentoDAO;
import br.gov.mec.aghu.model.FatItemContaHospitalar;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * 
 * <p>
 * ORADB: <code>FATC_BUSCA_CPF_RESP</code> <br/>
 * ORADB: <code>FATC_BUSCA_CBO</code> <br/>
 * </p>
 * @author gandriotti
 *
 */
@SuppressWarnings({"PMD.HierarquiaONRNIncorreta"})
@Stateless
public class BuscaCpfCboResponsavelPorIchRN extends AbstractFatDebugLogEnableRN {

private static final Log LOG = LogFactory.getLog(BuscaCpfCboResponsavelPorIchRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private FatItemContaHospitalarDAO fatItemContaHospitalarDAO;

@Inject
private VFatAssociacaoProcedimentoDAO vFatAssociacaoProcedimentoDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -7880149782878407543L;

	protected VFatAssociacaoProcedimentoDAO getVFatAssociacaoProcedimentoDAO() {

		return vFatAssociacaoProcedimentoDAO;
	}

	protected FatItemContaHospitalarDAO getFatItemContaHospitalarDAO() {

		return fatItemContaHospitalarDAO;
	}

	protected FatItemContaHospitalar obterIchPorCthSeqPhiSeqEIphSeq(final Integer cthSeq, final Integer iphSeq, final Short iphPhoSeq) {
		return this.getFatItemContaHospitalarDAO().obterPorCthSeqPhiSeqEIphSeq(cthSeq, iphSeq, iphPhoSeq);
	}

	protected Long obterCpfServidor(RapServidores servidor) {

		Long result = null;

		if ((servidor != null) && (servidor.getPessoaFisica() != null)) {
			result = servidor.getPessoaFisica().getCpf();
		}

		return result;
	}
	
	protected Short[] obterListaRapTipoInformacaoSeqContemCbo() throws ApplicationBusinessException {
		return this.buscarVlrShortArrayAghParametro(AghuParametrosEnum.P_TIPO_INFORMACAO_CONTEM_CBO);		
	}

	protected boolean verificarExistenciaCboServidor(RapServidores servidor) throws ApplicationBusinessException {
	
		boolean result = false;
		result = getRegistroColaboradorFacade().existePessoaFisicaComTipoInformacao(
				servidor.getPessoaFisica().getCodigo(),
				this.obterListaRapTipoInformacaoSeqContemCbo());
	
		return result;
	}

	protected RapServidores obterPrimeiroServidorAtendendoCriterioTii(
			FatItemContaHospitalar ich) throws ApplicationBusinessException {

		RapServidores result = null;
		RapServidores servidor = null;
		
		servidor = ich.getServidor();
		if ((servidor != null) && this.verificarExistenciaCboServidor(servidor)) {
			result = servidor;
		} else {
			servidor = ich.getServidorAnest();
			if ((servidor != null) && this.verificarExistenciaCboServidor(servidor)) {
				result = servidor;
			}
		}

		return result;
	}

	protected RapServidores obterPrimeiroResponsavelAtendendoCriterioTii(
			Integer cthSeq,
			Integer iphSeq,
			Short iphPhoSeq) throws ApplicationBusinessException {
	
		RapServidores result = null;
		FatItemContaHospitalar ich = null;
		
		ich = this.obterIchPorCthSeqPhiSeqEIphSeq(cthSeq, iphSeq, iphPhoSeq);
		if(ich != null) {
			result = this.obterPrimeiroServidorAtendendoCriterioTii(ich);
		}
		
		return result;
	}

	/**
	 * 
	 * ORADB: <code>FATC_BUSCA_CPF_RESP</code> <br/>
	 * @param cthSeq
	 * @param iphSeq
	 * @param iphPhoSeq
	 * @return
	 *  
	 */
	public Long buscarCpfResponsavel(
			Integer cthSeq,
			Integer iphSeq,
			Short iphPhoSeq) throws ApplicationBusinessException {

		Long result = null;
		RapServidores servidor = null;

		//check args
		if (cthSeq == null) {
			throw new IllegalArgumentException("Parametro cthSeq nao informado!!!");
		}
		if (iphSeq == null) {
			throw new IllegalArgumentException("Parametro iphSeq nao informado!!!");
		}
		if (iphPhoSeq == null) {
			throw new IllegalArgumentException("Parametro iphPhoSeq nao informado!!!");
		}
		//algo
		servidor = this.obterPrimeiroResponsavelAtendendoCriterioTii(cthSeq, iphSeq, iphPhoSeq);
		if (servidor != null) {
			result = this.obterCpfServidor(servidor);
		}

		return result;
	}
	
	/**
	 * 
	 * ORADB: <code>FATC_BUSCA_CBO</code> <br/>
	 * @param cthSeq
	 * @param iphSeq
	 * @param iphPhoSeq
	 * @return
	 *  
	 */
	public String buscarCboResponsavel(
			Integer cthSeq,
			Integer iphSeq,
			Short iphPhoSeq) throws ApplicationBusinessException {

		RapServidores servidor = null;
		FatItemContaHospitalar ich = null;
		String cbo = null;
		
		
		//check args
		if (cthSeq == null) {
			throw new IllegalArgumentException("Parametro cthSeq nao informado!!!");
		}
		if (iphSeq == null) {
			throw new IllegalArgumentException("Parametro iphSeq nao informado!!!");
		}
		if (iphPhoSeq == null) {
			throw new IllegalArgumentException("Parametro iphPhoSeq nao informado!!!");
		}
		//algo
		ich = this.obterIchPorCthSeqPhiSeqEIphSeq(cthSeq, iphSeq, iphPhoSeq);
		if(ich != null) {
			servidor = ich.getServidor();
			if (servidor != null) {
				cbo = this.getRegistroColaboradorFacade().buscaCbo(servidor.getId().getMatricula(), servidor.getId().getVinCodigo(), this.obterListaRapTipoInformacaoSeqContemCbo());
				if(cbo == null){
					servidor = ich.getServidorAnest();
					if (servidor != null) {
						cbo = this.getRegistroColaboradorFacade().buscaCbo(servidor.getId().getMatricula(), servidor.getId().getVinCodigo(), this.obterListaRapTipoInformacaoSeqContemCbo());
					}
				}
			}		
		}

		return cbo;
	}
	
}
