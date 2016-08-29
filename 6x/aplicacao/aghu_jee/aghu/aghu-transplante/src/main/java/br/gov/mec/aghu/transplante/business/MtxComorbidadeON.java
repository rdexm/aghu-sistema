package br.gov.mec.aghu.transplante.business;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;

import br.gov.mec.aghu.dominio.DominioSituacaoTmo;
import br.gov.mec.aghu.dominio.DominioTipoTransplante;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MtxComorbidade;
import br.gov.mec.aghu.model.MtxComorbidadePaciente;
import br.gov.mec.aghu.model.MtxTransplantes;
import br.gov.mec.aghu.transplante.dao.MtxComorbidadeDAO;
import br.gov.mec.aghu.transplante.dao.MtxComorbidadePacienteDAO;
import br.gov.mec.aghu.transplante.vo.PacienteTransplanteMedulaOsseaVO;
import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
public class MtxComorbidadeON extends BaseBusiness {
	
	
	private static final long serialVersionUID = -7609489731468126572L;
	
	@Inject
	private MtxComorbidadeDAO mtxComorbidadeDAO;
	
	@Inject
	private MtxComorbidadePacienteDAO mtxComorbidadePacienteDAO;
	
	private List<MtxComorbidade> listaComorbidade;

	@Override
	protected Log getLogger() {
		return null;
	}
	
	public MtxComorbidadeON() {
		
	}
	
	public List<MtxComorbidade> pesquisarDoenca(MtxComorbidade mtxComorbidade){
		listaComorbidade = mtxComorbidadeDAO.pesquisarDoenca(mtxComorbidade);
		return concatenarCID(listaComorbidade);
	}
	
	public List<MtxComorbidade> pesquisarComorbidade(MtxComorbidade mtxComorbidade, Integer firstResult, Integer maxResults, String orderProperty, boolean asc){
		listaComorbidade = mtxComorbidadeDAO.pesquisarComorbidadePorDescricaoTipoAtivo(mtxComorbidade, firstResult, maxResults, orderProperty, asc);
		return concatenarCID(listaComorbidade);
	}
	
	public List<MtxComorbidade> concatenarCID(List<MtxComorbidade> listaComorbidade){
		for(int x = 0; x < listaComorbidade.size(); x++){
			if(listaComorbidade.get(x).getCidSeq() != null){
				listaComorbidade.get(x).setConcatDescricao(listaComorbidade.get(x).getCidSeq().getCodigo() + " - " + listaComorbidade.get(x).getCidSeq().getDescricao());
			}else{
				listaComorbidade.get(x).setConcatDescricao(listaComorbidade.get(x).getDescricao());
			}
		}
		return listaComorbidade;
	}
	
	public PacienteTransplanteMedulaOsseaVO carregarDadosPaciente(Integer prontuario, Object tipo){
		AipPacientes tempPaciente;
		PacienteTransplanteMedulaOsseaVO pacienteTransplanteMedulaOsseaVO = new PacienteTransplanteMedulaOsseaVO();
		MtxTransplantes mtxTransplantes = mtxComorbidadeDAO.carregarDadosPaciente(prontuario, tipo).get(0);
		
		if(mtxTransplantes.getTipoTmo() != null){
			if(mtxTransplantes.getTipoTmo().toString().equals(DominioSituacaoTmo.G.toString())){
				pacienteTransplanteMedulaOsseaVO.setTipoAlogenico(mtxTransplantes.getTipoAlogenico());
				pacienteTransplanteMedulaOsseaVO.setTipo("Alogênico - "+mtxTransplantes.getTipoAlogenico().getDescricao());
				
			}else if(mtxTransplantes.getTipoTmo().toString().equals(DominioSituacaoTmo.U.toString())){
				pacienteTransplanteMedulaOsseaVO.setTipoTmo(mtxTransplantes.getTipoTmo());
				pacienteTransplanteMedulaOsseaVO.setTipo(mtxTransplantes.getTipoTmo().getDescricao());
			}
		}else{
			pacienteTransplanteMedulaOsseaVO.setTipoOrgao(mtxTransplantes.getTipoOrgao());
			pacienteTransplanteMedulaOsseaVO.setTipo(mtxTransplantes.getTipoOrgao().getDescricao());
		}
		tempPaciente = mtxTransplantes.getDoador() != null ? mtxTransplantes.getDoador() : mtxTransplantes.getReceptor();
		pacienteTransplanteMedulaOsseaVO.setAipPacientes(tempPaciente);
		pacienteTransplanteMedulaOsseaVO.setProntuario(tempPaciente.getProntuario());
		pacienteTransplanteMedulaOsseaVO.setNome(tempPaciente.getNome());
		pacienteTransplanteMedulaOsseaVO.setSituacao(mtxTransplantes.getSituacao());
		
		return pacienteTransplanteMedulaOsseaVO;
	}
	
	public List<MtxComorbidade> pesquisarComorbidadePorTipoDescricaoCid(MtxComorbidade mtxComorbidade){
		listaComorbidade = mtxComorbidadeDAO.pesquisarComorbidadePorTipoDescricaoCid(mtxComorbidade);
		return concatenarCID(listaComorbidade);
	}
	
	public List<MtxComorbidadePaciente> carregarComorbidadesPaciente(DominioTipoTransplante tipo, AipPacientes aipPacientes){
		return mtxComorbidadePacienteDAO.carregarComorbidadesPaciente(tipo, aipPacientes);
	}

}
