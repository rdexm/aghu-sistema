package br.gov.mec.aghu.blococirurgico.agendamento.business;





import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.MbcDescricaoPadraoDAO;
import br.gov.mec.aghu.model.MbcDescricaoPadrao;
import br.gov.mec.aghu.model.MbcDescricaoPadraoId;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.BaseException;


@Stateless
public class MbcDescricaoPadraoON extends BaseBusiness{

	private static final Log LOG = LogFactory.getLog(MbcDescricaoPadraoON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcDescricaoPadraoDAO mbcDescricaoPadraoDAO;


	@EJB
	private MbcDescricaoPadraoRN mbcDescricaoPadraoRN;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1403535177734724903L;

	
	public void persistirDescricaoPadrao(final MbcDescricaoPadrao mbcDescricaopadrao) throws BaseException {
       if(this.validarCamposObrigatorios(mbcDescricaopadrao)){
			if (mbcDescricaopadrao.getVersion() == null) {
				this.iterarSeqp(mbcDescricaopadrao);
				this.getMbcDescricaoPadraoRN().inserir(mbcDescricaopadrao);
			} else {
				this.getMbcDescricaoPadraoRN().atualizar(mbcDescricaopadrao);
			}
       }
	}
	
	private void iterarSeqp(MbcDescricaoPadrao mbcDescricaoPadrao) {
		Integer seq = 1;
		Integer maiorSeqp = getMbcDescricaoDAO().obterSequenceDescricaoPadrao(mbcDescricaoPadrao.getAghEspecialidades().getSeq());
		if(maiorSeqp!=null){
			seq = ++maiorSeqp;
		}
		mbcDescricaoPadrao.setId(new MbcDescricaoPadraoId());
		mbcDescricaoPadrao.getId().setSeqp(seq);
		mbcDescricaoPadrao.getId().setEspSeq(mbcDescricaoPadrao.getAghEspecialidades().getSeq());
	}
	
	private Boolean validarCamposObrigatorios(MbcDescricaoPadrao mbcDescricaopadrao){
		if(mbcDescricaopadrao.getAghEspecialidades()== null || mbcDescricaopadrao.getAghEspecialidades().getSeq()==null){
			return false;
		}
		
		if(mbcDescricaopadrao.getMbcProcedimentoCirurgicos() == null || mbcDescricaopadrao.getMbcProcedimentoCirurgicos().getSeq()==null){
			return false;
		}
		
		if(mbcDescricaopadrao.getDescricaoTecPadrao() == null || StringUtils.isEmpty(mbcDescricaopadrao.getDescricaoTecPadrao())){
			return false;
		}
		if(mbcDescricaopadrao.getTitulo() == null || StringUtils.isEmpty(mbcDescricaopadrao.getDescricaoTecPadrao())){
			return false;
		}
		return true;
	}
	
	
	
	public void remover(MbcDescricaoPadraoId id) throws BaseException {
		MbcDescricaoPadrao descricaoPadrao = getMbcDescricaoDAO().obterPorChavePrimaria(id);
		getMbcDescricaoPadraoRN().remover(descricaoPadrao);
	}
	
	private MbcDescricaoPadraoDAO getMbcDescricaoDAO() {
		return mbcDescricaoPadraoDAO;
	}
	
	private MbcDescricaoPadraoRN getMbcDescricaoPadraoRN(){
		return mbcDescricaoPadraoRN;
	}

	public MbcDescricaoPadrao obterPorChavePrimaria(MbcDescricaoPadraoId id) throws BaseException {
		return getMbcDescricaoPadraoRN().obterPorChavePrimaria(id);
	}
	
}
