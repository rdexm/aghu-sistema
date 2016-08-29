package br.gov.mec.aghu.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.configuracao.dao.AghAtendimentoDAO;
import br.gov.mec.aghu.configuracao.dao.AghCidDAO;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SigProcessamentoCusto;
import br.gov.mec.aghu.vo.AghAtendimentosVO;
import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
public class AghAtendimentoON extends BaseBusiness {
	
	private static final long serialVersionUID = 4094174011234897327L;

	private static final Log LOG = LogFactory.getLog(AghAtendimentoON.class);

	@Inject
	private AghCidDAO aghCidDAO;
	
	@Inject
	private AghAtendimentoDAO aghAtendimentoDAO;

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	public List<AghAtendimentosVO> obterAghAtendimentosPorFiltrosPaciente(Integer firstResult,Integer maxResult, String orderProperty, boolean asc,
			AipPacientes paciente, List<AghCid> listaCID,
			List<FccCentroCustos> listaCentroCusto,
			List<AghEspecialidades> listaEspecialidades,
			List<RapServidores> responsaveis) {
		
		if(listaCID != null) {
			listaCID = validaListaCID(listaCID);
		}
		return this.getAghAtendimentoDAO().obterAghAtendimentosPorFiltrosPacienteTelaVisualizarCustoPaciente(firstResult, maxResult, orderProperty, asc, paciente, listaCID, listaCentroCusto, listaEspecialidades, responsaveis);
	}

	public Long obterAghAtendimentosPorFiltrosPacienteCount(
			AipPacientes paciente, List<AghCid> listaCID,
			List<FccCentroCustos> listaCentroCusto,
			List<AghEspecialidades> listaEspecialidades,
			List<RapServidores> responsaveis) {
		if(listaCID != null) {
			listaCID = validaListaCID(listaCID);
		}
		List<AghAtendimentosVO> lista = this.getAghAtendimentoDAO().obterAghAtendimentosPorFiltrosPacienteTelaVisualizarCustoPaciente(null, null, null, false, paciente, listaCID, listaCentroCusto, listaEspecialidades, responsaveis);
		if(lista!=null){
			return Long.valueOf(lista.size());
		}
		return null;
	}
	
	public Long obterAghAtendimentosPorFiltrosCompetenciaTelaVisualizarCustoPacienteCount(
			SigProcessamentoCusto competenciaSelecionada, List<AghCid> listaCID, List<FccCentroCustos> listaCentroCusto, List<AghEspecialidades> listaEspecialidades, List<RapServidores> responsaveis,Boolean pacienteComAlta) {
		List<AghAtendimentosVO> lista = this.getAghAtendimentoDAO().obterAghAtendimentosPorFiltrosCompetenciaTelaVisualizarCustoPaciente(null, null, null, false, competenciaSelecionada, listaCID, listaCentroCusto, listaEspecialidades, responsaveis, pacienteComAlta);
		if(lista!=null){
			return Long.valueOf(lista.size());
		}
		return null;
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
		if(novaListaCID==null || novaListaCID.size()==0){
			novaListaCID.add(itemCID);
		}
		return novaListaCID;
	}

	public AghCidDAO getAghCidDAO() {
		return aghCidDAO;
	}

	public void setAghCidDAO(AghCidDAO aghCidDAO) {
		this.aghCidDAO = aghCidDAO;
	}

	public AghAtendimentoDAO getAghAtendimentoDAO() {
		return aghAtendimentoDAO;
	}

	public void setAghAtendimentoDAO(AghAtendimentoDAO aghAtendimentoDAO) {
		this.aghAtendimentoDAO = aghAtendimentoDAO;
	}

	/**
	 * Web Service #40705
	 * 
	 * Atualizar a data início do atendimento com a Data de Nascimento do Recém-Nascido na tabela AGH_ATENDIMENTOS
	 * 
	 * @param pacCodigo
	 * @param dthrInicio
	 * @param dthrNascimento
	 */
	public void atualizarAtendimentoDthrNascimento(final Integer pacCodigo, final Date dthrInicio, final Date dthrNascimento) {
		AghAtendimentos atendimento = getAghAtendimentoDAO().obterAtendimentoPorPacienteDataInicio(pacCodigo, dthrInicio);
		if (atendimento != null) {
			atendimento.setDthrInicio(dthrNascimento);
			getAghAtendimentoDAO().atualizar(atendimento);
		}
	}
}
