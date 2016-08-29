package br.gov.mec.aghu.business.prescricaoenfermagem;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.EpeCaractDefDiagnostico;
import br.gov.mec.aghu.model.EpeCaractDefinidora;
import br.gov.mec.aghu.model.EpeDiagnostico;
import br.gov.mec.aghu.model.EpeFatRelDiagnostico;
import br.gov.mec.aghu.model.EpeFatRelacionado;
import br.gov.mec.aghu.prescricaoenfermagem.dao.EpeCaractDefDiagnosticoDAO;
import br.gov.mec.aghu.prescricaoenfermagem.dao.EpeFatRelDiagnosticoDAO;
import br.gov.mec.aghu.prescricaoenfermagem.vo.DiagnosticoVO;
import br.gov.mec.aghu.prescricaoenfermagem.vo.EtiologiaVO;
import br.gov.mec.aghu.prescricaoenfermagem.vo.SinalSintomaVO;
import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
public class SinalSintomaON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(SinalSintomaON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private EpeCaractDefDiagnosticoDAO epeCaractDefDiagnosticoDAO;

@Inject
private EpeFatRelDiagnosticoDAO epeFatRelDiagnosticoDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2207783776322010695L;

	/**
	 * 
	 */
	
	public List<SinalSintomaVO> pesquisarSinaisSintomasPorGrupoSubgrupoEDescricao(Short dgnSnbGnbSeq, Short dgnSnbSequencia, String descricao) {
		List<EpeCaractDefDiagnostico> listaCaractDefDiagnosticos = this.getEpeCaractDefDiagnosticoDAO().pesquisarCaractDefDiagnosticoPorGrupoSubgrupoEDescricao(dgnSnbGnbSeq, dgnSnbSequencia, descricao);
		List<EpeCaractDefinidora> listaCaractDefinidora = null;
		Set<EpeCaractDefinidora> hashCaractDefinidora = new LinkedHashSet<EpeCaractDefinidora>();
		for(EpeCaractDefDiagnostico caractDefDiagnostico: listaCaractDefDiagnosticos){
			hashCaractDefinidora.add(caractDefDiagnostico.getCaractDefinidora());
		}
		if(hashCaractDefinidora.size()>0){
			listaCaractDefinidora = new ArrayList<EpeCaractDefinidora>(hashCaractDefinidora);
		} else {
			listaCaractDefinidora = new ArrayList<EpeCaractDefinidora>();
		}
		
		List<SinalSintomaVO> listaSinaisSintomas = new ArrayList<SinalSintomaVO>();
		
		for(int i=0; i< listaCaractDefinidora.size();i++){
			SinalSintomaVO sinalSintomaVO = new SinalSintomaVO();
			sinalSintomaVO.setCodigo(listaCaractDefinidora.get(i).getCodigo());
			sinalSintomaVO.setDescricao(listaCaractDefinidora.get(i).getDescricao());
			if(i==0){
				sinalSintomaVO.setSelecionado(true);
			} else {
				sinalSintomaVO.setSelecionado(false);
			}
			listaSinaisSintomas.add(sinalSintomaVO);
		}
		return listaSinaisSintomas;
		
		
	}
	
	public List<DiagnosticoVO> pesquisarDiagnosticoPorSinalSintoma(Integer codigo) {
		List<EpeCaractDefDiagnostico> listaCaractDefDiagnosticos = this.getEpeCaractDefDiagnosticoDAO().pesquisarCaractDefDiagnosticoPorCodigoCaractDefinidora(codigo);
		List<EpeDiagnostico> listaDiagnostico = null;
		Set<EpeDiagnostico> hashDiagnostico = new LinkedHashSet<EpeDiagnostico>();
		for(EpeCaractDefDiagnostico caractDefDiagnostico: listaCaractDefDiagnosticos){
			hashDiagnostico.add(caractDefDiagnostico.getDiagnostico());
		}
		if(hashDiagnostico.size()>0){
			listaDiagnostico = new ArrayList<EpeDiagnostico>(hashDiagnostico);
		} else {
			listaDiagnostico = new ArrayList<EpeDiagnostico>();
		}
		
		List<DiagnosticoVO> listaDiagnosticos = new ArrayList<DiagnosticoVO>();
		for(int i=0; i<listaDiagnostico.size(); i++){
			DiagnosticoVO diagnosticoVO = new DiagnosticoVO();
			diagnosticoVO.setSequencia(listaDiagnostico.get(i).getId().getSequencia());
			diagnosticoVO.setSnbGnbSeq(listaDiagnostico.get(i).getId().getSnbGnbSeq());
			diagnosticoVO.setSnbSequencia(listaDiagnostico.get(i).getId().getSnbSequencia());
			diagnosticoVO.setDescricao(listaDiagnostico.get(i).getDescricao());
			if(i==0){
				diagnosticoVO.setSelecionado(true);
			} else {
				diagnosticoVO.setSelecionado(false);
			}
			listaDiagnosticos.add(diagnosticoVO);
		}
		return listaDiagnosticos;
	}
	
	public List<EtiologiaVO> pesquisarEtiologiaPorDiagnostico(Short snbGnbSeq, Short snbSequencia, Short sequencia) {
		List<EpeFatRelDiagnostico> listaFatRelDiagnostico = this.getEpeFatRelDiagnosticoDAO().pesquisarFatRelDiagnosticoPorDiagnostico(snbGnbSeq, snbSequencia, sequencia);
		List<EpeFatRelacionado> listaFatRelacionado = null;
		Set<EpeFatRelacionado> hashFatRelacionado = new LinkedHashSet<EpeFatRelacionado>();
		for(EpeFatRelDiagnostico fatRelDiagnostico: listaFatRelDiagnostico){
			hashFatRelacionado.add(fatRelDiagnostico.getFatRelacionado());
		}
		if(hashFatRelacionado.size()>0){
			listaFatRelacionado = new ArrayList<EpeFatRelacionado>(hashFatRelacionado);
		} else {
			listaFatRelacionado = new ArrayList<EpeFatRelacionado>();
		}
		
		
		List<EtiologiaVO> listaEtiologia = new ArrayList<EtiologiaVO>();
		for(EpeFatRelacionado fatRelacionado: listaFatRelacionado){
			EtiologiaVO etiologiaVO = new EtiologiaVO();
			etiologiaVO.setSeq(fatRelacionado.getSeq());
			etiologiaVO.setDescricao(fatRelacionado.getDescricao());
			etiologiaVO.setSelecionada(false);
			listaEtiologia.add(etiologiaVO);
		}
		return listaEtiologia;
	}
	
	protected EpeCaractDefDiagnosticoDAO getEpeCaractDefDiagnosticoDAO() {
		return epeCaractDefDiagnosticoDAO;
	}
	
	protected EpeFatRelDiagnosticoDAO getEpeFatRelDiagnosticoDAO() {
		return epeFatRelDiagnosticoDAO;
	}
}
