package br.gov.mec.aghu.compras.business;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.dao.ScoMarcaModeloMaterialDAO;
import br.gov.mec.aghu.estoque.vo.MarcaModeloMaterialVO;
import br.gov.mec.aghu.model.ScoMarcaModelo;
import br.gov.mec.aghu.model.ScoMarcaModeloMaterial;
import br.gov.mec.aghu.model.ScoMarcaModeloMaterialId;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class CadastrarMarcaModeloMaterialRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(CadastrarMarcaModeloMaterialRN.class);
	
	private static final long serialVersionUID = 4392312394108296909L;
	
	public enum CadastrarMarcaModeloMaterialRNExceptionCode implements BusinessExceptionCode {
		MSG_M1_MARCA_MODELO, MSG_M3_MARCA_MODELO, MSG_M4_MARCA_MODELO;
	}

	@Inject
	private ScoMarcaModeloMaterialDAO scoMarcaModeloMaterialDAO;
	
	public List<MarcaModeloMaterialVO> pesquisarMarcaModeloMaterial(Integer codigoMaterial, Integer codigoMarca, Integer codigoModelo) throws ApplicationBusinessException {
		
		List<MarcaModeloMaterialVO> listaMarcaModelo = new ArrayList<MarcaModeloMaterialVO>();
		List<Object[]> resultQuery = this.getScoMarcaModeloMaterialDAO().pesquisarMarcaModeloMaterial(codigoMaterial, codigoMarca, codigoModelo);
		
		if(resultQuery.isEmpty()){
			throw new ApplicationBusinessException(CadastrarMarcaModeloMaterialRNExceptionCode.MSG_M1_MARCA_MODELO);
		}
		
		for (Object[] obj : resultQuery) {
			MarcaModeloMaterialVO vo = new MarcaModeloMaterialVO();
			
			vo.setCodigoMaterial(Integer.parseInt(obj[0].toString()));
			vo.setNomeMaterial(obj[1].toString());
			vo.setCodigoMarcaComercial(Integer.parseInt(obj[2].toString()));
			vo.setDescricaoMarcaComercial(obj[3].toString());
			vo.setSeqpMarcaModelo(Integer.parseInt(obj[4].toString()));
			vo.setDescricaoMarcaModelo(obj[5].toString());
			vo.setCodigoMarcaModelo(Integer.parseInt(obj[6].toString()));
			
			listaMarcaModelo.add(vo);
		}
		
		return listaMarcaModelo;
	}

	
	public void adicionarMarcaModelo(ScoMarcaModelo marcaModelo, ScoMaterial material) throws ApplicationBusinessException {
		
		if(marcaModelo == null){
			throw new ApplicationBusinessException(CadastrarMarcaModeloMaterialRNExceptionCode.MSG_M3_MARCA_MODELO);
		}
		
		List<Object[]> resultQuery = this.getScoMarcaModeloMaterialDAO().pesquisarMarcaModeloMaterial(material.getCodigo(), marcaModelo.getId().getMcmCodigo(), marcaModelo.getId().getSeqp());
		
		if(!resultQuery.isEmpty()){
			throw new ApplicationBusinessException(CadastrarMarcaModeloMaterialRNExceptionCode.MSG_M4_MARCA_MODELO);
		}
		
		ScoMarcaModeloMaterialId id = new ScoMarcaModeloMaterialId();
		id.setMatCodigo(material.getCodigo());
		id.setMomMcmCodigo(marcaModelo.getId().getMcmCodigo());
		id.setMomSeqp(marcaModelo.getId().getSeqp());
		
		ScoMarcaModeloMaterial marcaModeloMaterial = new ScoMarcaModeloMaterial();
		marcaModeloMaterial.setId(id);
		marcaModeloMaterial.setVersion(0);
		
		this.getScoMarcaModeloMaterialDAO().persistir(marcaModeloMaterial);
		this.getScoMarcaModeloMaterialDAO().flush();
	}
	
	public void excluirMarcaModelo(MarcaModeloMaterialVO marcaModelo, ScoMaterial material){
		
		ScoMarcaModeloMaterialId id = new ScoMarcaModeloMaterialId();
		id.setMatCodigo(material.getCodigo());
		id.setMomMcmCodigo(marcaModelo.getCodigoMarcaModelo());
		id.setMomSeqp(marcaModelo.getSeqpMarcaModelo());
		
		ScoMarcaModeloMaterial marcaModeloMaterial = this.getScoMarcaModeloMaterialDAO().obterPorChavePrimaria(id);
		this.getScoMarcaModeloMaterialDAO().remover(marcaModeloMaterial);
		this.getScoMarcaModeloMaterialDAO().flush();
	}
	
	private ScoMarcaModeloMaterialDAO getScoMarcaModeloMaterialDAO() {
		return scoMarcaModeloMaterialDAO;
	}
	
	@Override
	protected Log getLogger() {
		return LOG;
	}
	
}
