package br.gov.mec.aghu.prescricaoenfermagem.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.prescricaoenfermagem.IPrescricaoEnfermagemFacade;
import br.gov.mec.aghu.model.EpeDiagnostico;
import br.gov.mec.aghu.model.EpeFatRelacionado;
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

public class DiagnosticoController extends ActionController {
	
	private static final String MANTER_PRESCRICAO_ENFERMAGEM = "prescricaoenfermagem-manterPrescricaoEnfermagem";
	private static final String SELECIONAR_CUIDADOS_DIAGNOSTICO = "prescricaoenfermagem-selecionarCuidadosDiagnostico";
	private static final long serialVersionUID = -1691876289762077962L;
	private static final Log LOG = LogFactory.getLog(DiagnosticoController.class);
	
	@EJB
	private IPrescricaoEnfermagemFacade prescricaoEnfermagemFacade;
	
	@Inject
	private PrescricaoEnfermagemTemplateController prescricaoEnfermagemTemplateController;
	
	private Integer atdSeq;
	
	private Integer seq;
	
	private Short gnbSeq;
	
	private Short snbSequencia;
	
	private Short sequencia;

	private EpeGrupoNecesBasica grupoNecesBasica;
	
	private EpeSubgrupoNecesBasica subgrupoNecesBasica;
	
	private EpeDiagnostico diagnostico;
	
	private EpeFatRelacionado etiologia;
	
	private List<DiagnosticoVO> listaDiagnosticos;
	
	private List<EtiologiaVO> listaEtiologias;
	
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
		this.listaDiagnosticos = null;
		this.listaEtiologias = null;
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
		if(diagnostico!=null){
			sequencia = diagnostico.getId().getSequencia();
		}else{
			sequencia = null;
		}
		this.listaDiagnosticos = this.pesquisarDiagnostico(gnbSeq, snbSequencia, null, sequencia);
		if(listaDiagnosticos!=null && !listaDiagnosticos.isEmpty()){
			DiagnosticoVO diagnosticoSelecionado = listaDiagnosticos.get(0);
			sequenciaDiagnostico = diagnosticoSelecionado.getSequencia();
			snbGnbSeqDiagnostico = diagnosticoSelecionado.getSnbGnbSeq();
			snbSequenciaDiagnostico = diagnosticoSelecionado.getSnbSequencia();
			this.listaEtiologias = this.pesquisarEtiologiaPorDiagnostico(diagnosticoSelecionado.getSnbGnbSeq(), diagnosticoSelecionado.getSnbSequencia(), diagnosticoSelecionado.getSequencia());	
		} else {
			listaEtiologias = null;
		}
		this.primeiraPesquisa = true;	
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
		this.diagnostico = null;
		this.etiologia = null;
		this.listaDiagnosticos = null;
		this.listaEtiologias = null;
		this.primeiraPesquisa = false;
	}
	
	public String cancelarPesquisa() {
		return null;
	}
	
	public void limparPorGrupo(){
		this.subgrupoNecesBasica = null;
		this.diagnostico = null;
		this.etiologia = null;
		this.listaDiagnosticos = null;
		this.listaEtiologias = null;
		this.primeiraPesquisa = false;
	}
	
	public void limparPorSubgrupo(){
		this.diagnostico = null;
		this.etiologia = null;
		this.listaDiagnosticos = null;
		this.listaEtiologias = null;
		this.primeiraPesquisa = false;
	}
	
	public void limparPorDiagnostico(){
		this.etiologia = null;
		this.listaDiagnosticos = null;
		this.listaEtiologias = null;
		this.primeiraPesquisa = false;
	}
	
	public void limparPorEtiologia(){
		this.listaDiagnosticos = null;
		this.listaEtiologias = null;
		this.primeiraPesquisa = false;
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
	
	public List<EpeDiagnostico> pesquisarDiagnosticos(String filtro){
		Short gnbSeq = null;
		if(grupoNecesBasica!=null){
			gnbSeq= grupoNecesBasica.getSeq();
		}
		Short snbSequencia = null;
		if(subgrupoNecesBasica!=null){
			snbSequencia= subgrupoNecesBasica.getId().getSequencia();
		}
		
		return this.returnSGWithCount(this.getPrescricaoEnfermagemFacade().pesquisarDiagnosticos(filtro, gnbSeq, snbSequencia),
				this.getPrescricaoEnfermagemFacade().pesquisarDiagnosticos(filtro, gnbSeq, snbSequencia).size());
	}
	
	public List<EpeFatRelacionado> pesquisarEtiologias(String filtro){
		Short gnbSeq = null;
		if(grupoNecesBasica!=null){
			gnbSeq= grupoNecesBasica.getSeq();
		}
		Short snbSequencia = null;
		if(subgrupoNecesBasica!=null){
			snbSequencia= subgrupoNecesBasica.getId().getSequencia();
		}
		Short sequencia = null;
		if(diagnostico!=null){
			sequencia= diagnostico.getId().getSequencia();
		}
		if(gnbSeq==null || snbSequencia==null || sequencia == null){
			return null;
		} else {
			return this.returnSGWithCount(this.getPrescricaoEnfermagemFacade().pesquisarEtiologias(filtro, gnbSeq, snbSequencia, sequencia),
					this.getPrescricaoEnfermagemFacade().pesquisarEtiologias(filtro, gnbSeq, snbSequencia,sequencia).size());	
		}
	}
	
	public List<SinalSintomaVO> pesquisarSinaisSintomasPorGrupoSubgrupoEDescricao(Short dgnSnbGnbSeq, Short dgnSnbSequencia, String descricao) {
		return this.getPrescricaoEnfermagemFacade().pesquisarSinaisSintomasPorGrupoSubgrupoEDescricao(dgnSnbGnbSeq, dgnSnbSequencia, descricao);
	}
	
	public List<DiagnosticoVO> pesquisarDiagnosticos(Short dgnSnbGnbSeq, Short dgnSnbSequencia, String dgnDescricao, Short dgnSequencia) {
		return this.getPrescricaoEnfermagemFacade().pesquisarDiagnosticos(dgnSnbGnbSeq, dgnSnbSequencia, dgnDescricao, dgnSequencia);
	}
	
	public List<EtiologiaVO> pesquisarEtiologiaPorDiagnostico(Short snbGnbSeq, Short snbSequencia, Short sequencia) {
		return this.getPrescricaoEnfermagemFacade().pesquisarEtiologiaPorDiagnostico(snbGnbSeq, snbSequencia, sequencia);
	}
	
	public String selecionarCuidados() {
		return SELECIONAR_CUIDADOS_DIAGNOSTICO;
	}
	
	public String voltarLista(){
		limparPesquisa();
		return MANTER_PRESCRICAO_ENFERMAGEM;
	}
	
	public SelecaoCuidadoVO carregarSelecaoCuidadoVO(){
		this.selecaoCuidadoVO = new SelecaoCuidadoVO();
		this.selecaoCuidadoVO.setPenSeq(seq);
		this.selecaoCuidadoVO.setAtdSeq(atdSeq);
		List<Short> lista = new ArrayList<Short>();
		if(listaEtiologias!=null){
			for(EtiologiaVO etiologiaVO:listaEtiologias){
				if(etiologiaVO.getSelecionada()){
					lista.add(etiologiaVO.getSeq());
				}
			}	
		} else {
			sequenciaDiagnostico = this.getDiagnostico().getId().getSequencia();
			snbGnbSeqDiagnostico = this.getDiagnostico().getId().getSnbGnbSeq();
			snbSequenciaDiagnostico = this.getDiagnostico().getId().getSnbSequencia();
		}
		this.selecaoCuidadoVO.setDgnSequencia(sequenciaDiagnostico);
		this.selecaoCuidadoVO.setDgnSnbGnbSeq(snbGnbSeqDiagnostico);
		this.selecaoCuidadoVO.setDgnSnbSequencia(snbSequenciaDiagnostico);
		
		if(this.etiologia!=null){
			lista.add(this.etiologia.getSeq());
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
	
	public List<DiagnosticoVO> pesquisarDiagnostico(Short gnbSeq, Short snbSequencia, String descricao, Short sequencia){
		return this.prescricaoEnfermagemFacade.pesquisarDiagnosticos(gnbSeq, snbSequencia, descricao, sequencia);
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
		return prescricaoEnfermagemTemplateController.getPrescricaoEnfermagemVO();
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

	public EpeDiagnostico getDiagnostico() {
		return diagnostico;
	}

	public void setDiagnostico(EpeDiagnostico diagnostico) {
		this.diagnostico = diagnostico;
	}

	public EpeFatRelacionado getEtiologia() {
		return etiologia;
	}

	public void setEtiologia(EpeFatRelacionado etiologia) {
		this.etiologia = etiologia;
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
	
	public Short getSequencia() {
		return sequencia;
	}

	public void setSequencia(Short sequencia) {
		this.sequencia = sequencia;
	}
}
