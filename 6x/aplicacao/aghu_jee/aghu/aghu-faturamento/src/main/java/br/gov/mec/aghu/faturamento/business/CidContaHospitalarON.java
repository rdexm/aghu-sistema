package br.gov.mec.aghu.faturamento.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioPrioridadeCid;
import br.gov.mec.aghu.faturamento.business.exception.FaturamentoExceptionCode;
import br.gov.mec.aghu.faturamento.dao.FatCidContaHospitalarDAO;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.FatCidContaHospitalar;
import br.gov.mec.aghu.model.FatCidContaHospitalarId;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;

@Stateless
public class CidContaHospitalarON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(CidContaHospitalarON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private FatCidContaHospitalarDAO fatCidContaHospitalarDAO;

@EJB
private CidContaHospitalarPersist cidContaHospitalarPersist;

	/**
	 * 
	 */
	private static final long serialVersionUID = -9000298682952917591L;
	/**
	 * @param dataFimVinculoServidor 
	 * Método para persistir uma CID conta hospitalar (somente faz insert)
	 * @param newCidCtaHosp
	 * @throws BaseException 
	 * @throws  
	 */
	public void persistirCidContaHospitalar(FatCidContaHospitalar newCidCtaHosp, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException{
		getCidContaHospitalarPersist().setComFlush(false);
		getCidContaHospitalarPersist().inserir(newCidCtaHosp, nomeMicrocomputador, dataFimVinculoServidor);
	}

	/**
	 * Verifica se tem mais que um cid igual cadastrado
	 * 
	 * @param cthSeq
	 * @param cidSeq
	 * @throws ApplicationBusinessException
	 */
	public void buscaCountQtdCids(Integer cthSeq,
			Integer cidSeq) throws ApplicationBusinessException {
		
		Long count = getFatCidContaHospitalarDAO().buscaCountQtdCids(cthSeq, cidSeq);
		if (count > 0) {
			throw new ApplicationBusinessException(FaturamentoExceptionCode.CID_JA_CADASTRADO);
		}
	}
	
	/**
	 * Método para buscar Cids através de sua descrição ou código
	 * 
	 * @param descricao
	 * @return
	 */
	public List<AghCid> pesquisarCidsPorDescricaoOuCodigo(String descricao,
			Integer limiteRegistros) {
		return getFatCidContaHospitalarDAO().pesquisarCidsPorDescricaoOuCodigo(descricao, limiteRegistros);
	}	
	
	public Long pesquisarCidsPorDescricaoOuCodigoCount(String descricao) {
		return getFatCidContaHospitalarDAO().pesquisarCidsPorDescricaoOuCodigoCount(descricao);
	}	
	
	public void removerCidContaHospitalar(Integer cthSeq, Integer cidSeq, DominioPrioridadeCid prioridadeCid, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException{
		
		FatCidContaHospitalarId id = new FatCidContaHospitalarId(cthSeq, cidSeq, prioridadeCid);
		
		getCidContaHospitalarPersist().setComFlush(false);
		getCidContaHospitalarPersist().remover(obterCid(id), nomeMicrocomputador, dataFimVinculoServidor);
	}
	
	public FatCidContaHospitalar obterCid(FatCidContaHospitalarId id) {
		return getFatCidContaHospitalarDAO().obterPorChavePrimaria(id);
	}
	
	protected FatCidContaHospitalarDAO getFatCidContaHospitalarDAO(){
		return fatCidContaHospitalarDAO;
	}
	protected CidContaHospitalarPersist getCidContaHospitalarPersist(){
		return cidContaHospitalarPersist;
	}

	/**
	 * Método para buscar Cids através de sua descrição ou código e SSM realizado
	 * 
	 * @param descricao
	 * @return
	 */
	public List<AghCid> pesquisarCidsPorSSMDescricaoOuCodigo(Integer phiSeq, String descricao, Integer limiteRegistros) {
		return getFatCidContaHospitalarDAO().pesquisarCidsPorSSMDescricaoOuCodigo(phiSeq, descricao, limiteRegistros);
	}	

	public Long pesquisarCidsPorSSMDescricaoOuCodigoCount(Integer phiSeq,String descricao) {
		return getFatCidContaHospitalarDAO().pesquisarCidsPorSSMDescricaoOuCodigoCount(phiSeq, descricao);
	}	
}