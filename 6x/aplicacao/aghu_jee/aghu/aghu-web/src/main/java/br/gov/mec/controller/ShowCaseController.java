package br.gov.mec.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.event.ValueChangeEvent;
import javax.imageio.ImageIO;
import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.util.Base64;

import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.casca.model.Menu;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.dominio.DominioModuloCompetencia;
import br.gov.mec.aghu.dominio.DominioMotivoPendencia;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatCompetencia;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;

public class ShowCaseController extends ActionController implements ActionPaginator{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1434160036849757999L;

	private static final Log LOG = LogFactory.getLog(ShowCaseController.class);
	
	private String texto;
	private Long numero;
	private Long numeroDecimal;
	private Date mesAno;
	private Long cpf;
	private Long cnpj;
	private Long cep;
	private Long prontuario;
	private String info;
	private String mask;
	private AipPacientes paciente;
	private String area;
	private Boolean checkbox;
	private DominioSimNao combobox;
	private DominioMotivoPendencia radio;
	private String imagemBase64;
	private String nomeImagemAbrir;
	private String nomeImagemSalvar;
	
	private List<Menu> listaChecked;
	
	private FatCompetencia competencia;
	
	@EJB
	private IFaturamentoFacade faturamentoFacade;

	@EJB
	private ICascaFacade cascaFacade;

	@EJB
	private IPacienteFacade pacienteFacade;
	
	@Inject @Paginator
	private DynamicDataModel<Menu> dataModel;

	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
		dataModel.reiniciarPaginator();
	}

	public void abrirImagemEditor() {
		File file = new File(nomeImagemAbrir);
        Path source = Paths.get(file.getPath());
		byte[] fileArray;
		try {
			fileArray = FileUtils.readFileToByteArray(file);
            String stringBase64 = Base64.encodeToString(fileArray, false);
			imagemBase64 = "data:"+Files.probeContentType(source)+";base64,"+stringBase64;
		} catch (IOException e) {
			LOG.error("Erro ao ler imagem: ",e);
		}
	}
	
    public void salvarImagemEditor() {
        String[] stringImagemEditor = imagemBase64.split(",");
        byte[] imagemGravacao = Base64.decode(stringImagemEditor[1]); 
        
        BufferedImage image;
		try {
			image = ImageIO.read(new ByteArrayInputStream(imagemGravacao));
			if (image == null) {
	             LOG.error("Erro ao criar Buffered Image. ");
	         }
	        File f = new File(nomeImagemSalvar);
	         ImageIO.write(image, "png", f);
	         image.flush();
		} catch (IOException e) {
			LOG.error("Erro ao salvar imagem.", e);
		}
    }
    
	public void pesquisaPaciente(ValueChangeEvent event) {
		try {
			paciente = pacienteFacade.pesquisarPacienteComponente(
					event.getNewValue(), event.getComponent().getId());
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	@Override
	public Long recuperarCount() {
		return cascaFacade.pesquisarMenucrudCount(null, null , null, null);
	}

	@Override	
	public List<Menu> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return cascaFacade.pesquisarMenu(null, null , null, null, firstResult, maxResult, orderProperty, asc);
	}
	
	public List<FatCompetencia> pesquisarCompetencias(String objPesquisa) {
		try {
			return this.returnSGWithCount(faturamentoFacade.listarCompetenciaModuloMesAnoDtHoraInicioSemHora(faturamentoFacade.getCompetenciaId(objPesquisa,
					DominioModuloCompetencia.AMB)),pesquisarCompetenciasCount(objPesquisa));
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return new ArrayList<FatCompetencia>(0);
		}
	}
	
	public Long pesquisarCompetenciasCount(String objPesquisa) {
		try {
			return faturamentoFacade.listarCompetenciaModuloMesAnoDtHoraInicioSemHoraCount(faturamentoFacade.getCompetenciaId(objPesquisa,
					DominioModuloCompetencia.AMB));
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return 0L;
		}
	}
	
	// metodos de auxílio
	public void multipla() {
		if(listaChecked != null) {
			listaChecked.size();
		}
	}

	public String voltar() {
		return null;
	}
	
	public String getTexto() {
		return texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}

	public Long getNumero() {
		return numero;
	}

	public void setNumero(Long numero) {
		this.numero = numero;
	}

	public Long getNumeroDecimal() {
		return numeroDecimal;
	}

	public void setNumeroDecimal(Long numeroDecimal) {
		this.numeroDecimal = numeroDecimal;
	}

	public Date getMesAno() {
		return mesAno;
	}

	public void setMesAno(Date mesAno) {
		this.mesAno = mesAno;
	}

	public Long getCpf() {
		return cpf;
	}

	public void setCpf(Long cpf) {
		this.cpf = cpf;
	}

	public Long getCnpj() {
		return cnpj;
	}

	public void setCnpj(Long cnpj) {
		this.cnpj = cnpj;
	}

	public Long getCep() {
		return cep;
	}

	public void setCep(Long cep) {
		this.cep = cep;
	}

	public Long getProntuario() {
		return prontuario;
	}

	public void setProntuario(Long prontuario) {
		this.prontuario = prontuario;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public FatCompetencia getCompetencia() {
		return competencia;
	}

	public void setCompetencia(FatCompetencia competencia) {
		this.competencia = competencia;
	}

	public DynamicDataModel<Menu> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<Menu> dataModel) {
		this.dataModel = dataModel;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getMask() {
		return mask;
	}

	public void setMask(String mask) {
		this.mask = mask;
	}

	public AipPacientes getPaciente() {
		return paciente;
	}

	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
	}

	public Boolean getCheckbox() {
		return checkbox;
	}

	public void setCheckbox(Boolean checkbox) {
		this.checkbox = checkbox;
	}

	public DominioSimNao getCombobox() {
		return combobox;
	}

	public void setCombobox(DominioSimNao combobox) {
		this.combobox = combobox;
	}

	public DominioMotivoPendencia getRadio() {
		return radio;
	}

	public void setRadio(DominioMotivoPendencia radio) {
		this.radio = radio;
	}

	public List<Menu> getListaChecked() {
		return listaChecked;
	}

	public void setListaChecked(List<Menu> listaChecked) {
		this.listaChecked = listaChecked;
	}

	public String getImagemBase64() {
		return imagemBase64;
	}

	public void setImagemBase64(String imagemBase64) {
		this.imagemBase64 = imagemBase64;
	}

	public String getNomeImagemAbrir() {
		return nomeImagemAbrir;
	}

	public void setNomeImagemAbrir(String nomeImagemAbrir) {
		this.nomeImagemAbrir = nomeImagemAbrir;
	}

	public String getNomeImagemSalvar() {
		return nomeImagemSalvar;
	}

	public void setNomeImagemSalvar(String nomeImagemSalvar) {
		this.nomeImagemSalvar = nomeImagemSalvar;
	}
	
}
