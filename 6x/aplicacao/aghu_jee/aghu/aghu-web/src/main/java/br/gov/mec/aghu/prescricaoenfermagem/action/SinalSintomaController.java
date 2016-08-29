package br.gov.mec.aghu.prescricaoenfermagem.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.prescricaoenfermagem.IPrescricaoEnfermagemFacade;
import br.gov.mec.aghu.model.EpeGrupoNecesBasica;
import br.gov.mec.aghu.model.EpePrescricaoEnfermagem;
import br.gov.mec.aghu.model.EpePrescricaoEnfermagemId;
import br.gov.mec.aghu.model.EpeSubgrupoNecesBasica;
import br.gov.mec.aghu.prescricaoenfermagem.cadastrosapoio.action.PrescricaoEnfermagemTemplateController;
import br.gov.mec.aghu.prescricaoenfermagem.vo.DiagnosticoVO;
import br.gov.mec.aghu.prescricaoenfermagem.vo.EtiologiaVO;
import br.gov.mec.aghu.prescricaoenfermagem.vo.PrescricaoEnfermagemVO;
import br.gov.mec.aghu.prescricaoenfermagem.vo.SelecaoCuidadoVO;
import br.gov.mec.aghu.prescricaoenfermagem.vo.SinalSintomaVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;


public class SinalSintomaController extends ActionController {

	private static final String SELECIONAR_CUIDADOS_DIAGNOSTICO = "prescricaoenfermagem-selecionarCuidadosDiagnostico";

	private static final String MANTER_PRESCRICAO_ENFERMAGEM = "prescricaoenfermagem-manterPrescricaoEnfermagem";

	private static final Log LOG = LogFactory.getLog(SinalSintomaController.class);
	
	private static final long serialVersionUID = -1691876289762077962L;

	@EJB
	private IPrescricaoEnfermagemFacade prescricaoEnfermagemFacade;
	
	@Inject
	private PrescricaoEnfermagemTemplateController prescricaoEnfermagemTemplateController;
	
	private Integer atdSeq;
	
	private Integer seq;
	
	private Short gnbSeq;
	
	private Short snbSequencia;
	
	private EpeGrupoNecesBasica grupoNecesBasica;
	
	private EpeSubgrupoNecesBasica subgrupoNecesBasica;
	
	private String descricao;
	
	private List<SinalSintomaVO> listaSinaisSintomas;
	
	private List<DiagnosticoVO> listaDiagnosticos;
	
	private List<EtiologiaVO> listaEtiologias;
	
	private Integer codigoSinalSintoma;
	
	private Short sequenciaDiagnostico;
	
	private Short snbGnbSeqDiagnostico;
	
	private Short snbSequenciaDiagnostico;
	
	private Boolean primeiraPesquisa;
	
	private SelecaoCuidadoVO selecaoCuidadoVO;
	
	private Boolean existeEtiologia;
	
	private int idConversacaoAnterior;
	
	@PostConstruct
	protected void inicializar(){
		LOG.debug("begin conversation");
		this.begin(conversation);
	}
	
	
	public void pesquisar(){
		this.listaSinaisSintomas = null;
		this.listaDiagnosticos = null;
		this.listaEtiologias = null;
		selecaoCuidadoVO = new SelecaoCuidadoVO();
		if(grupoNecesBasica!=null){
			gnbSeq = grupoNecesBasica.getSeq();
		} else {
			gnbSeq = null;
		}
		if(subgrupoNecesBasica!=null){
			snbSequencia = subgrupoNecesBasica.getId().getSequencia();
		} else {
			snbSequencia = null;
		}
		this.listaSinaisSintomas = this.pesquisarSinaisSintomasPorGrupoSubgrupoEDescricao(gnbSeq, snbSequencia, descricao);
		
		if(listaSinaisSintomas!=null && !listaSinaisSintomas.isEmpty()){
			codigoSinalSintoma = listaSinaisSintomas.get(0).getCodigo();
			this.listaDiagnosticos = this.pesquisarDiagnosticoPorSinalSintoma(codigoSinalSintoma);
			if(listaDiagnosticos!=null && !listaDiagnosticos.isEmpty()){
				DiagnosticoVO diagnosticoSelecionado = listaDiagnosticos.get(0);
				sequenciaDiagnostico = diagnosticoSelecionado.getSequencia();
				snbGnbSeqDiagnostico = diagnosticoSelecionado.getSnbGnbSeq();
				snbSequenciaDiagnostico = diagnosticoSelecionado.getSnbSequencia();
				this.listaEtiologias = this.pesquisarEtiologiaPorDiagnostico(diagnosticoSelecionado.getSnbGnbSeq(), diagnosticoSelecionado.getSnbSequencia(), diagnosticoSelecionado.getSequencia());	
			} else {
				listaEtiologias = null;
			}
		} else{
			listaDiagnosticos = null;
			listaEtiologias = null;
		}
			this.primeiraPesquisa = true;	
	}
	
	public void pesquisarPorSinalSintoma(){
		this.listaDiagnosticos = this.pesquisarDiagnosticoPorSinalSintoma(codigoSinalSintoma);
		if(listaDiagnosticos!=null && !listaDiagnosticos.isEmpty()){
			DiagnosticoVO diagnosticoSelecionado = listaDiagnosticos.get(0);
			sequenciaDiagnostico = diagnosticoSelecionado.getSequencia();
			snbGnbSeqDiagnostico = diagnosticoSelecionado.getSnbGnbSeq();
			snbSequenciaDiagnostico = diagnosticoSelecionado.getSnbSequencia();
			this.listaEtiologias = this.pesquisarEtiologiaPorDiagnostico(diagnosticoSelecionado.getSnbGnbSeq(), diagnosticoSelecionado.getSnbSequencia(), diagnosticoSelecionado.getSequencia());	
		}
	}
	
	public void pesquisarPorDiagnostico(){
			this.listaEtiologias = this.pesquisarEtiologiaPorDiagnostico(snbGnbSeqDiagnostico, snbSequenciaDiagnostico, sequenciaDiagnostico);	
	}
	
	
	public void iniciar() {
	 

		try{
			EpePrescricaoEnfermagemId id = new EpePrescricaoEnfermagemId();
			if(this.seq!=null && this.atdSeq!=null){
				id.setAtdSeq(atdSeq);
				id.setSeq(seq);
				EpePrescricaoEnfermagem prescricaoEnfermagem = this.prescricaoEnfermagemFacade.obterPrescricaoEnfermagemPorId(id);
				this.prescricaoEnfermagemTemplateController.setPrescricaoEnfermagemVO(this.prescricaoEnfermagemFacade.popularDadosCabecalhoPrescricaoEnfermagemVO(prescricaoEnfermagem));		
			}	
		} catch(ApplicationBusinessException exception){
			this.apresentarExcecaoNegocio(exception);
		}
	
	}
	
	public void limparPesquisa() {
		this.grupoNecesBasica = null;
		this.subgrupoNecesBasica = null;
		this.descricao = "";
		this.listaSinaisSintomas = null;
		this.listaDiagnosticos = null;
		this.listaEtiologias = null;
		this.primeiraPesquisa = false;
	}
	
	public String cancelarPesquisa() {
		return null;
	}
	
	
	public String voltarLista(){
		limparPesquisa();
		return MANTER_PRESCRICAO_ENFERMAGEM;
	}
	
	public void limparSubgrupo(){
		this.subgrupoNecesBasica = null;		
	}
	
	public List<EpeGrupoNecesBasica> pesquisarGrupos(String filtro){
		return this.returnSGWithCount(this.getPrescricaoEnfermagemFacade().pesquisarGruposNecesBasica(filtro),
				this.getPrescricaoEnfermagemFacade().pesquisarGruposNecesBasica(filtro).size());
	}
	
	public List<EpeSubgrupoNecesBasica> pesquisarSubgrupos(String filtro){
		Short gnbSeq = null;
		if(grupoNecesBasica!=null){
			gnbSeq= grupoNecesBasica.getSeq();
		}
		return this.returnSGWithCount(this.getPrescricaoEnfermagemFacade().pesquisarSubgruposNecesBasica(filtro, gnbSeq),
				this.getPrescricaoEnfermagemFacade().pesquisarSubgruposNecesBasica(filtro, gnbSeq).size());
	}

	public List<SinalSintomaVO> pesquisarSinaisSintomasPorGrupoSubgrupoEDescricao(Short dgnSnbGnbSeq, Short dgnSnbSequencia, String descricao) {
		return this.getPrescricaoEnfermagemFacade().pesquisarSinaisSintomasPorGrupoSubgrupoEDescricao(dgnSnbGnbSeq, dgnSnbSequencia, descricao);
	}
	
	public List<DiagnosticoVO> pesquisarDiagnosticoPorSinalSintoma(Integer codigo) {
		return this.getPrescricaoEnfermagemFacade().pesquisarDiagnosticoPorSinalSintoma(codigo);
	}
	
	public List<EtiologiaVO> pesquisarEtiologiaPorDiagnostico(Short snbGnbSeq, Short snbSequencia, Short sequencia) {
		return this.getPrescricaoEnfermagemFacade().pesquisarEtiologiaPorDiagnostico(snbGnbSeq, snbSequencia, sequencia);
	}
	
	public String selecionarCuidados() {
		return SELECIONAR_CUIDADOS_DIAGNOSTICO;
	}
	
	public SelecaoCuidadoVO carregarSelecaoCuidadoVO(){
		this.selecaoCuidadoVO.setPenSeq(seq);
		this.selecaoCuidadoVO.setAtdSeq(atdSeq);
		this.selecaoCuidadoVO.setDgnSequencia(sequenciaDiagnostico);
		this.selecaoCuidadoVO.setDgnSnbGnbSeq(snbGnbSeqDiagnostico);
		this.selecaoCuidadoVO.setDgnSnbSequencia(snbSequenciaDiagnostico);
		List<Short> lista = new ArrayList<Short>();
		for(EtiologiaVO etiologiaVO:listaEtiologias){
			if(etiologiaVO.getSelecionada()){
				lista.add(etiologiaVO.getSeq());
			}
		}
		this.selecaoCuidadoVO.setEtiologias(lista);
		return this.selecaoCuidadoVO;
	}
	
	
	public void verificarEtiologia(){
		for(EtiologiaVO etiologiaVO:listaEtiologias){
			if(etiologiaVO.getSelecionada()){
				existeEtiologia = true;
				return;
			}
		}
		existeEtiologia = false;
	}
	
	public IPrescricaoEnfermagemFacade getPrescricaoEnfermagemFacade() {
		return prescricaoEnfermagemFacade;
	}

	public void setPrescricaoEnfermagemFacade(
			IPrescricaoEnfermagemFacade prescricaoEnfermagemFacade) {
		this.prescricaoEnfermagemFacade = prescricaoEnfermagemFacade;
	}

	public Integer getAtdSeq() {
		return atdSeq;
	}

	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}

	public Short getGnbSeq() {
		return gnbSeq;
	}

	public void setGnbSeq(Short gnbSeq) {
		this.gnbSeq = gnbSeq;
	}

	public PrescricaoEnfermagemVO getPrescricaoEnfermagemVO() {
		return this.prescricaoEnfermagemTemplateController.getPrescricaoEnfermagemVO();
	}

	public void setPrescricaoEnfermagemVO(
			PrescricaoEnfermagemVO prescricaoEnfermagemVO) {
		this.prescricaoEnfermagemTemplateController.setPrescricaoEnfermagemVO(prescricaoEnfermagemVO);
	}
	
	

	public EpeGrupoNecesBasica getGrupoNecesBasica() {
		return grupoNecesBasica;
	}

	public void setGrupoNecesBasica(EpeGrupoNecesBasica grupoNecesBasica) {
		this.grupoNecesBasica = grupoNecesBasica;
	}

	public EpeSubgrupoNecesBasica getSubgrupoNecesBasica() {
		return subgrupoNecesBasica;
	}

	public void setSubgrupoNecesBasica(EpeSubgrupoNecesBasica subgrupoNecesBasica) {
		this.subgrupoNecesBasica = subgrupoNecesBasica;
	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public List<SinalSintomaVO> getListaSinaisSintomas() {
		return listaSinaisSintomas;
	}

	public void setListaSinaisSintomas(
			List<SinalSintomaVO> listaSinaisSintomas) {
		this.listaSinaisSintomas = listaSinaisSintomas;
	}

	public List<DiagnosticoVO> getListaDiagnosticos() {
		return listaDiagnosticos;
	}

	public void setListaDiagnosticos(List<DiagnosticoVO> listaDiagnosticos) {
		this.listaDiagnosticos = listaDiagnosticos;
	}

	public List<EtiologiaVO> getListaEtiologias() {
		return listaEtiologias;
	}

	public void setListaEtiologias(List<EtiologiaVO> listaEtiologias) {
		this.listaEtiologias = listaEtiologias;
	}


	public Short getSnbSequencia() {
		return snbSequencia;
	}


	public void setSnbSequencia(Short snbSequencia) {
		this.snbSequencia = snbSequencia;
	}

	public Integer getCodigoSinalSintoma() {
		return codigoSinalSintoma;
	}

	public void setCodigoSinalSintoma(Integer codigoSinalSintoma) {
		this.codigoSinalSintoma = codigoSinalSintoma;
	}

	public Short getSequenciaDiagnostico() {
		return sequenciaDiagnostico;
	}

	public void setSequenciaDiagnostico(Short sequenciaDiagnostico) {
		this.sequenciaDiagnostico = sequenciaDiagnostico;
	}

	public Short getSnbGnbSeqDiagnostico() {
		return snbGnbSeqDiagnostico;
	}

	public void setSnbGnbSeqDiagnostico(Short snbGnbSeqDiagnostico) {
		this.snbGnbSeqDiagnostico = snbGnbSeqDiagnostico;
	}

	public Short getSnbSequenciaDiagnostico() {
		return snbSequenciaDiagnostico;
	}

	public void setSnbSequenciaDiagnostico(Short snbSequenciaDiagnostico) {
		this.snbSequenciaDiagnostico = snbSequenciaDiagnostico;
	}

	public Boolean getPrimeiraPesquisa() {
		return primeiraPesquisa;
	}

	public void setPrimeiraPesquisa(Boolean primeiraPesquisa) {
		this.primeiraPesquisa = primeiraPesquisa;
	}

	public Boolean getExisteEtiologia() {
		return existeEtiologia;
	}

	public void setExisteEtiologia(Boolean existeEtiologia) {
		this.existeEtiologia = existeEtiologia;
	}

	public int getIdConversacaoAnterior() {
		return idConversacaoAnterior;
	}

	public void setIdConversacaoAnterior(int idConversacaoAnterior) {
		this.idConversacaoAnterior = idConversacaoAnterior;
	}
	
	public SelecaoCuidadoVO getSelecaoCuidadoVO() {
		return selecaoCuidadoVO;
	}

	public void setSelecaoCuidadoVO(SelecaoCuidadoVO selecaoCuidadoVO) {
		this.selecaoCuidadoVO = selecaoCuidadoVO;
	}	
}
