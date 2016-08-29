package br.gov.mec.aghu.sig.custos.business;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.configuracao.dao.AghCidDAO;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.AghEquipes;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.sig.custos.vo.CalculoAtendimentoPacienteVO;
import br.gov.mec.aghu.sig.dao.SigCalculoAtdPaciente2DAO;
import br.gov.mec.aghu.sig.dao.SigCalculoAtdPacienteDAO;
import br.gov.mec.aghu.sig.dao.SigCalculoAtdReceitaDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
public class CalculoAtendimentoPacienteON  extends BaseBusiness {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2956231442962728855L;

	private static final Log LOG = LogFactory.getLog(CalculoAtendimentoPacienteON.class);
	
	@Override
	protected Log getLogger() {
		return LOG;
	}

	@Inject
	private SigCalculoAtdPacienteDAO sigCalculoAtdPacienteDAO;
	
	@Inject
	private SigCalculoAtdPaciente2DAO sigCalculoAtdPaciente2DAO;
	
	@Inject
	private SigCalculoAtdReceitaDAO sigCalculoAtdReceitaDAO;
	
	@Inject
	private AghCidDAO aghCidDAO;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	public List<CalculoAtendimentoPacienteVO> buscarEquipesMedicas(Integer prontuario, Integer pmuSeq, Integer atdSeq, List<Integer> seqCategorias) {
		List<CalculoAtendimentoPacienteVO> lista = this.sigCalculoAtdPacienteDAO.buscarEquipesMedicas(prontuario, pmuSeq, atdSeq, seqCategorias);
		
		for(CalculoAtendimentoPacienteVO item : lista) {
			List<AghEquipes> equipes = aghuFacade.pesquisarEquipesPorMatriculaVinculo(item.getMatriculaRespEquipe(), item.getVinCodigoRespEquipe());
			
			if(!equipes.isEmpty()) {
				item.setNomeEquipe(equipes.get(0).getNome());
			}
		}
		
		return lista;
	}
	
	public List<CalculoAtendimentoPacienteVO> pesquisarReceitaPorEquipeMedica(Integer prontuario, Integer pmuSeq, Integer atdSeq) {
		List<CalculoAtendimentoPacienteVO> lista = this.sigCalculoAtdReceitaDAO.pesquisarReceitaPorEquipeMedica(prontuario, pmuSeq, atdSeq);
		
		for(CalculoAtendimentoPacienteVO item : lista) {
			List<AghEquipes> equipes = aghuFacade.pesquisarEquipesPorMatriculaVinculo(item.getMatriculaRespEquipe(), item.getVinCodigoRespEquipe());
			
			if(!equipes.isEmpty()) {
				item.setNomeEquipe(equipes.get(0).getNome());
			}
		}
		
		return lista;
	}
	
	public BigDecimal buscarCustoTotalPesquisa(Integer prontuario, Integer pmuSeq, List<AghCid> listaCID, List<FccCentroCustos> listaCentroCusto, List<AghEspecialidades> listaEspecialidades, List<RapServidores> responsaveis) {
		
		if(listaCID != null) {
			listaCID = validaListaCID(listaCID);
		}
		return getSigCalculoAtdPaciente2DAO().buscarCustoTotalPesquisa(prontuario, pmuSeq, listaCID, listaCentroCusto, listaEspecialidades, responsaveis);
	}
	
	public BigDecimal buscarReceitaTotalPesquisa(Integer prontuario, Integer pmuSeq, List<AghCid> listaCID, List<FccCentroCustos> listaCentroCusto, List<AghEspecialidades> listaEspecialidades, List<RapServidores> responsaveis) {
		
		if(listaCID != null) {
			listaCID = validaListaCID(listaCID);
		}
		return getSigCalculoAtdPaciente2DAO().buscarReceitaTotalPesquisa(prontuario, pmuSeq, listaCID, listaCentroCusto, listaEspecialidades, responsaveis);
	}
	
	
	private List<AghCid> validaListaCID(List<AghCid> listaCID) {
		List<AghCid> novaListaCID = new ArrayList<AghCid>();
		for(AghCid itemCID:listaCID) {
			novaListaCID.addAll(validaCDICategoria(itemCID));
		}
		return novaListaCID;
	}

	private List<AghCid> validaCDICategoria(AghCid itemCID) {
		List<AghCid> novaListaCID = new ArrayList<AghCid>();
		if(itemCID.getCid() != null) {
			novaListaCID.add(itemCID);
		} else {
			novaListaCID = this.getAghCidDAO().buscarCidsVinculadosCategoria(itemCID.getSeq());
		}
		return novaListaCID;
	}

	public AghCidDAO getAghCidDAO() {
		return aghCidDAO;
	}

	public void setAghCidDAO(AghCidDAO aghCidDAO) {
		this.aghCidDAO = aghCidDAO;
	}

	public SigCalculoAtdPaciente2DAO getSigCalculoAtdPaciente2DAO() {
		return sigCalculoAtdPaciente2DAO;
	}

	public void setSigCalculoAtdPaciente2DAO(
			SigCalculoAtdPaciente2DAO sigCalculoAtdPaciente2DAO) {
		this.sigCalculoAtdPaciente2DAO = sigCalculoAtdPaciente2DAO;
	}
	
	

}
