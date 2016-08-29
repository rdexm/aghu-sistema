package br.gov.mec.aghu.blococirurgico.cadastroapoio.business;


import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.MbcSalaCirurgicaDAO;
import br.gov.mec.aghu.model.MbcSalaCirurgica;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.BaseException;

@Stateless
public class SalaCirurgicaON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(SalaCirurgicaON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcSalaCirurgicaDAO mbcSalaCirurgicaDAO;


	@EJB
	private SalaCirurgicaRN salaCirurgicaRN;
	/**
	 * 
	 */
	private static final long serialVersionUID = -3550201868576718062L;

	public void persistirSalaCirurgica(final MbcSalaCirurgica mbcCirurgica) throws BaseException {
//		this.validarCamposObrigatorios(mbcHorarioTurnoCirg);
		if (mbcCirurgica.getVersion() == null) {
			this.iterarSeqp(mbcCirurgica);
			this.getSalaCirurgicaRN().inserir(mbcCirurgica);
		} else {
			this.getSalaCirurgicaRN().atualizar(mbcCirurgica);
		}
	}

	private void iterarSeqp(MbcSalaCirurgica mbcCirurgica) {
		Short maiorSeqp = getMbcSalaCirurgicaDAO().obterSequenceSalaCirurgica(mbcCirurgica.getUnidadeFuncional().getSeq());
		if(maiorSeqp!=null){
			maiorSeqp++;
		}else{
			maiorSeqp = 1;
		}
		mbcCirurgica.getId().setSeqp(maiorSeqp);
	}
	
	private MbcSalaCirurgicaDAO getMbcSalaCirurgicaDAO() {
		return mbcSalaCirurgicaDAO;
	}

	protected SalaCirurgicaRN getSalaCirurgicaRN() {
		return salaCirurgicaRN;
	}

	

}
