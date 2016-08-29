package br.gov.mec.aghu.exames.business;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacaoAmostra;
import br.gov.mec.aghu.exames.dao.AelExtratoAmostrasDAO;
import br.gov.mec.aghu.exames.dao.AelExtratoAmostrasHistDAO;
import br.gov.mec.aghu.exames.vo.AelExtratoAmostrasVO;
import br.gov.mec.aghu.model.AelAmostras;
import br.gov.mec.aghu.model.AelExtratoAmostras;
import br.gov.mec.aghu.model.AelExtratoAmostrasHist;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.BaseException;

@Stateless
public class AelExtratoAmostrasON extends BaseBusiness {


@Inject
private AelExtratoAmostrasHistDAO aelExtratoAmostrasHistDAO;


@EJB
private AelExtratoAmostrasRN aelExtratoAmostrasRN;

private static final Log LOG = LogFactory.getLog(AelExtratoAmostrasON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AelExtratoAmostrasDAO aelExtratoAmostrasDAO;

@EJB
private IRegistroColaboradorFacade registroColaboradorFacade;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7540567975255688941L;

	
	public void inserirAelExtratoAmostra(AelExtratoAmostras aelExtratoAmostras) throws BaseException {
		getAelExtratoAmostrasRN().inserirAelExtratoAmostra(aelExtratoAmostras);
	}

	public List buscarAelExtratosAmostrasPorAmostra(Integer amoSoeSeq, Short amoSeqp, Boolean isHist) {
		if(isHist){
			return getAelExtratoAmostrasHistDAO().buscarAelExtratosAmostrasPorAmostra(amoSoeSeq,amoSeqp);
		}else{
			return getAelExtratoAmostrasDAO().buscarAelExtratosAmostrasPorAmostra(amoSoeSeq,amoSeqp);
		}
	}
	
	public List<AelExtratoAmostrasVO> buscarAelExtratosAmostrasVOPorAmostra(Integer amoSoeSeq, Short amoSeqp, Boolean isHist) {
		List<AelExtratoAmostrasVO> listVO = null;
		
			List list = this.buscarAelExtratosAmostrasPorAmostra(amoSoeSeq, amoSeqp, isHist);
			if(list != null && !list.isEmpty()){
				listVO = new LinkedList<AelExtratoAmostrasVO>();
				
				if(isHist){
					for (AelExtratoAmostrasHist extrato : new ArrayList<AelExtratoAmostrasHist>(list)) {
						AelExtratoAmostrasVO vo = new AelExtratoAmostrasVO();
		
						vo.setAmoSoeSeq(extrato.getId().getAmoSoeSeq());
						vo.setSeqp(extrato.getId().getSeqp());
						vo.setAmoSeqp(extrato.getId().getAmoSeqp());
						vo.setCriadoEm(extrato.getCriadoEm());
						if(extrato.getMatricula() != null){
							RapServidores serv = getRegistroColaboradorFacade().obterRapServidorPorVinculoMatricula(extrato.getMatricula(), extrato.getVinCodigo());
							vo.setServidorResponsavel(serv.getPessoaFisica().getNome());
						}
						if (extrato.getSituacao() != null){
							vo.setSituacao(DominioSituacaoAmostra.valueOf(extrato.getSituacao()));
						}
						
						listVO.add(vo);
					}
				}else{
					for (AelExtratoAmostras extrato : new ArrayList<AelExtratoAmostras>(list)) {
						AelExtratoAmostrasVO vo = new AelExtratoAmostrasVO();
		
						vo.setAmoSoeSeq(extrato.getId().getAmoSoeSeq());
						vo.setSeqp(extrato.getId().getSeqp());
						vo.setAmoSeqp(extrato.getId().getAmoSeqp());
						vo.setCriadoEm(extrato.getCriadoEm());
						if(extrato.getServidor() != null){
							vo.setServidorResponsavel(extrato.getServidor().getPessoaFisica().getNome());
						}
						if (extrato.getSituacao() != null){
							vo.setSituacao(extrato.getSituacao());
						}
						
						listVO.add(vo);
					}
				}
			}
		return listVO;
	}
	
	public List<AelExtratoAmostras> buscarAelExtratosAmostrasPorAmostra(
			AelAmostras aelAmostras) {
		return getAelExtratoAmostrasDAO().buscarAelExtratosAmostrasPorAmostra(aelAmostras);
	}
	
	public AelExtratoAmostrasRN getAelExtratoAmostrasRN(){
		return aelExtratoAmostrasRN;
	}

	public AelExtratoAmostrasDAO getAelExtratoAmostrasDAO(){
		return aelExtratoAmostrasDAO;
	}
	
	public AelExtratoAmostrasHistDAO getAelExtratoAmostrasHistDAO(){
		return aelExtratoAmostrasHistDAO;
	}
	
	public IRegistroColaboradorFacade getRegistroColaboradorFacade(){
		return registroColaboradorFacade;
	}
}
