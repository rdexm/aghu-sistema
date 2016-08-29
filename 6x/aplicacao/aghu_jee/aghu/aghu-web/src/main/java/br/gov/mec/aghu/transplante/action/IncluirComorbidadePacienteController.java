package br.gov.mec.aghu.transplante.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.dominio.DominioSituacaoTmo;
import br.gov.mec.aghu.dominio.DominioTipoOrgao;
import br.gov.mec.aghu.dominio.DominioTipoTransplante;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MtxComorbidade;
import br.gov.mec.aghu.model.MtxComorbidadePaciente;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.transplante.business.ITransplanteFacade;
import br.gov.mec.aghu.transplante.vo.PacienteTransplanteMedulaOsseaVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class IncluirComorbidadePacienteController extends ActionController {
	
	private static final long serialVersionUID = -128553908232510272L;

	private static final String PAGE_LISTAR_TRANSPLANTES = "listarTransplantes";
	private static final String PAGE_LISTAR_TRANSPLANTES_ORGAO = "listarTransplantesOrgao";
	
	
	@EJB
	private ITransplanteFacade transplanteFacade;

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	private PacienteTransplanteMedulaOsseaVO pacienteTransplanteMedulaOsseaVO;
	
	private List<MtxComorbidade> listaComorbidade = new ArrayList<MtxComorbidade>();
	private List<MtxComorbidade> listaComorbidadesExcluidas = new ArrayList<MtxComorbidade>();
	private List<MtxComorbidadePaciente> listaComorbidadePaciente;
	private MtxComorbidadePaciente mtxComorbidadePaciente;
	private MtxComorbidade mtxComorbidadeSelect;
	private MtxComorbidade mtxComorbidade;
	private MtxComorbidade mtxComorbidadePesquisa;
	private Integer numProntuario;
	private DominioTipoOrgao dominioTipoOrgao;
	private DominioSituacaoTmo dominioSituacaoTmo;
	private boolean voltarParaOrgaos;
	
	@PostConstruct
	public void init() {
		begin(conversation, true);
		iniciar();
	}
	
	public void iniciar(){
		if(this.numProntuario != null){
			carregarDadosPaciente();
			carregarComorbidadesPaciente(pacienteTransplanteMedulaOsseaVO.getTipoTmo() != null ? DominioTipoTransplante.M : DominioTipoTransplante.O, pacienteTransplanteMedulaOsseaVO.getAipPacientes());
		}
	}
	
	public String gravar(){
		List<MtxComorbidade> listaComorbidadeSalvar = new ArrayList<MtxComorbidade>();
		RapServidores servidor = servidorLogadoFacade.obterServidorLogado();
		if(!transplanteFacade.validarGravarComorbidadePaciente(listaComorbidade, listaComorbidadesExcluidas)){
			return null;
		}
		try {
			excluir();
			listaComorbidadeSalvar.addAll(listaComorbidade);
			listaComorbidadeSalvar = transplanteFacade.removerComorbidadePacienteJaInseridas(listaComorbidadeSalvar, pacienteTransplanteMedulaOsseaVO.getAipPacientes());
			for (MtxComorbidade mtxComorbidade : listaComorbidadeSalvar) {
				mtxComorbidadePaciente = new MtxComorbidadePaciente(pacienteTransplanteMedulaOsseaVO.getAipPacientes(), mtxComorbidade);
				mtxComorbidadePaciente.setCriadoEm(new Date());
				mtxComorbidadePaciente.setServidor(servidor);
				transplanteFacade.gravarComorbidadePaciente(mtxComorbidadePaciente);
				listaComorbidadePaciente.add(mtxComorbidadePaciente);
			}
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_INCLUSAO_COMORBIDADE_PACIENTE");
			return voltarPagPesquisa();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}
	
	public String voltarPagPesquisa() {
		if(voltarParaOrgaos){
			return PAGE_LISTAR_TRANSPLANTES_ORGAO;
		}else{
			return PAGE_LISTAR_TRANSPLANTES;
		}
	}

	public String excluir(){
		List<MtxComorbidade> listaComorbidadeTemp = new ArrayList<MtxComorbidade>();
		listaComorbidadeTemp.addAll(listaComorbidadesExcluidas);
		try {
			for (MtxComorbidade mtxComorbidade : listaComorbidadeTemp) {
				mtxComorbidadePaciente = new MtxComorbidadePaciente(pacienteTransplanteMedulaOsseaVO.getAipPacientes(), mtxComorbidade);
				if(listaComorbidadePaciente.contains(mtxComorbidadePaciente)){
					transplanteFacade.excluirComorbidadePaciente(mtxComorbidadePaciente);
					listaComorbidadesExcluidas.remove(mtxComorbidade);
					listaComorbidadePaciente.remove(mtxComorbidadePaciente);
				}
			}
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}
	
	public void adicionar() {
		if(mtxComorbidade == null){
			return;
		}
		if(listaComorbidade.contains(mtxComorbidade)){
			this.apresentarMsgNegocio(Severity.WARN, "COMORBIDADE_JA_SELECIONADA");
		}else{
			listaComorbidade.add(mtxComorbidade);
			listaComorbidadesExcluidas.remove(mtxComorbidade);
		}
		mtxComorbidade = null;
	}
	
	public void remover(MtxComorbidade mtxComorbidade){
		listaComorbidade.remove(mtxComorbidade);
		listaComorbidadesExcluidas.add(mtxComorbidade);
	}
	
	public void carregarDadosPaciente(){
		pacienteTransplanteMedulaOsseaVO = transplanteFacade.carregarDadosPaciente(numProntuario, dominioSituacaoTmo != null ? dominioSituacaoTmo : dominioTipoOrgao);
	}
	
	public List<MtxComorbidade> pesquisarComorbidade(String doenca) {
		mtxComorbidadePesquisa = new MtxComorbidade();
		mtxComorbidadePesquisa.setTipo(pacienteTransplanteMedulaOsseaVO.getTipoTmo() != null ? DominioTipoTransplante.M : DominioTipoTransplante.O);
		mtxComorbidadePesquisa.setDescricao(doenca);
		return this.returnSGWithCount(transplanteFacade.pesquisarComorbidadePorTipoDescricaoCid(mtxComorbidadePesquisa), 
				transplanteFacade.pesquisarComorbidadePorTipoDescricaoCidCount(mtxComorbidadePesquisa));
	}
	
	public void carregarComorbidadesPaciente(DominioTipoTransplante tipo, AipPacientes aipPacientes){
		listaComorbidade = new ArrayList<MtxComorbidade>();
		listaComorbidadePaciente = transplanteFacade.carregarComorbidadesPaciente(tipo, aipPacientes);
		for (MtxComorbidadePaciente mtxComorbidadePaciente : listaComorbidadePaciente) {
			listaComorbidade.add(mtxComorbidadePaciente.getCmbSeq());
		}
		listaComorbidade = transplanteFacade.concatenarCID(listaComorbidade);
	}

	public String obterHint(String string, Short tamanho){
        if(string.length() > tamanho){
            return StringUtils.abbreviate(string, tamanho); 
        }
        return string;
	}
	
	public PacienteTransplanteMedulaOsseaVO getPacienteTransplanteMedulaOsseaVO() {
		return pacienteTransplanteMedulaOsseaVO;
	}

	public void setPacienteTransplanteMedulaOsseaVO(PacienteTransplanteMedulaOsseaVO pacienteTransplanteMedulaOsseaVO) {
		this.pacienteTransplanteMedulaOsseaVO = pacienteTransplanteMedulaOsseaVO;
	}

	public MtxComorbidadePaciente getMtxComorbidadePaciente() {
		return mtxComorbidadePaciente;
	}

	public void setMtxComorbidadePaciente(
			MtxComorbidadePaciente mtxComorbidadePaciente) {
		this.mtxComorbidadePaciente = mtxComorbidadePaciente;
	}


	public MtxComorbidade getMtxComorbidadeSelect() {
		return mtxComorbidadeSelect;
	}

	public void setMtxComorbidadeSelect(MtxComorbidade mtxComorbidadeSelect) {
		this.mtxComorbidadeSelect = mtxComorbidadeSelect;
	}

	public List<MtxComorbidade> getListaComorbidade() {
		return listaComorbidade;
	}

	public void setListaComorbidade(List<MtxComorbidade> listaComorbidade) {
		this.listaComorbidade = listaComorbidade;
	}

	public List<MtxComorbidade> getListaComorbidadesExcluidas() {
		return listaComorbidadesExcluidas;
	}

	public void setListaComorbidadesExcluidas(List<MtxComorbidade> listaComorbidadesExcluidas) {
		this.listaComorbidadesExcluidas = listaComorbidadesExcluidas;
	}

	public MtxComorbidade getMtxComorbidade() {
		return mtxComorbidade;
	}

	public void setMtxComorbidade(MtxComorbidade mtxComorbidade) {
		this.mtxComorbidade = mtxComorbidade;
	}

	public Integer getNumProntuario() {
		return numProntuario;
	}

	public void setNumProntuario(Integer numProntuario) {
		this.numProntuario = numProntuario;
	}

	public boolean isVoltarParaOrgaos() {
		return voltarParaOrgaos;
	}

	public void setVoltarParaOrgaos(boolean voltarParaOrgaos) {
		this.voltarParaOrgaos = voltarParaOrgaos;
	}

	public DominioTipoOrgao getDominioTipoOrgao() {
		return dominioTipoOrgao;
	}

	public void setDominioTipoOrgao(DominioTipoOrgao dominioTipoOrgao) {
		this.dominioTipoOrgao = dominioTipoOrgao;
	}

	public DominioSituacaoTmo getDominioSituacaoTmo() {
		return dominioSituacaoTmo;
	}

	public void setDominioSituacaoTmo(DominioSituacaoTmo dominioSituacaoTmo) {
		this.dominioSituacaoTmo = dominioSituacaoTmo;
	}
}
