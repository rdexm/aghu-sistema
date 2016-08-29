package br.gov.mec.aghu.paciente.prontuarioonline.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import br.gov.mec.aghu.dominio.DominioFichasDocsDigitalizados;
import br.gov.mec.aghu.dominio.DominioOrigemDocsDigitalizados;
import br.gov.mec.aghu.model.AipOrigemDocGEDs;
import br.gov.mec.aghu.paciente.prontuarioonline.business.IProntuarioOnlineFacade;
import br.gov.mec.aghu.paciente.prontuarioonline.digitalizacao.business.DocumentoGEDVO;
import br.gov.mec.aghu.paciente.prontuarioonline.digitalizacao.business.IDigitalizacaoPOLFacade;
import br.gov.mec.aghu.paciente.prontuarioonline.digitalizacao.business.ParametrosGEDAdministrativosVO;
import br.gov.mec.aghu.paciente.prontuarioonline.digitalizacao.business.ParametrosGEDAtivosVO;
import br.gov.mec.aghu.paciente.prontuarioonline.digitalizacao.business.ParametrosGEDInativosVO;
import br.gov.mec.aghu.paciente.prontuarioonline.digitalizacao.business.ParametrosGEDVO;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.NodoPOLVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.business.SelectionQualifier;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

public class ConsultarDocDigitalizadoPOLController extends ActionController {

	private static final long serialVersionUID = 66673392775113873L;

	@EJB
	private IDigitalizacaoPOLFacade digitalizacaoPOLFacade;

	@EJB
	private IProntuarioOnlineFacade prontuarioOnlineFacade;


	private Integer prontuario;
	private DominioOrigemDocsDigitalizados origemEnum;
	private DominioFichasDocsDigitalizados ficha;

	private List<DocumentoGEDVO> documentos;
	
	@Inject @SelectionQualifier @RequestScoped
	private NodoPOLVO itemPOL;	
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	/**
	 * Metodo de inicializacao da controller, onde o valor do prontuario e atribuido via parametro.
	 */
	public void inicio() {
	 
		if (itemPOL!=null){
			prontuario = itemPOL.getProntuario();
		}
		
		if (prontuario != null) {
			ficha = DominioFichasDocsDigitalizados.valueOf(itemPOL.getParametros().get(NodoPOLVO.FICHA).toString());
			
			if(itemPOL.getParametros().get(NodoPOLVO.ORIGEM_DIGITALIZACAO) != null){
				origemEnum = DominioOrigemDocsDigitalizados.valueOf(itemPOL.getParametros().get(NodoPOLVO.ORIGEM_DIGITALIZACAO).toString());
			}
			
			pesquisarDocDigitalizado();
		}
	}

	/**
	 * Metodo que executa a chamada ao web service de visualizacao de um prontuario selecionado.
	 */
	public void pesquisarDocDigitalizado() {
		try {
			ParametrosGEDVO parametros;
			switch (ficha) {
				case INATIVOS:
					parametros = new ParametrosGEDInativosVO(((Integer) itemPOL.getParametros().get(NodoPOLVO.COD_PACIENTE)), null, 
															 prontuario.toString(), null, null, null, null,
															 obterLoginUsuarioLogado());
					
					documentos = digitalizacaoPOLFacade.buscaUrlsDocumentosGEDInativos(parametros);
				break;
					
				case ATIVOS:
					AipOrigemDocGEDs origemFiltro = new AipOrigemDocGEDs();
					origemFiltro.setOrigem(origemEnum);
					List<AipOrigemDocGEDs> origensMapeada = prontuarioOnlineFacade.pesquisarAipOrigemDocGEDs(origemFiltro, 0, 1, AipOrigemDocGEDs.Fields.ORIGEM.toString(), false);
					
					parametros = new ParametrosGEDVO();
					if (origensMapeada != null && origensMapeada.size() > 0 && origensMapeada.get(0) != null) {
						parametros = new ParametrosGEDAtivosVO(((Integer) itemPOL.getParametros().get(NodoPOLVO.COD_PACIENTE)), 
															   prontuario.toString(), null, origensMapeada.get(0).getReferencia(),
															   null, null, null, obterLoginUsuarioLogado());
					}
					documentos = digitalizacaoPOLFacade.buscaUrlsDocumentosGEDAtivos(parametros);
				break;
				
				case ADMINISTRATIVOS:
					
					// A String "Ambulatorio" de referência no sistema GED foi criada sem acento para este caso como já está em uso não há como alterar.
					
					parametros = new ParametrosGEDAdministrativosVO(((Integer) itemPOL.getParametros().get(NodoPOLVO.COD_PACIENTE)), 
																	prontuario.toString(), null, 
																	(DominioOrigemDocsDigitalizados.INT.equals(origemEnum) ? "Internação" : "Ambulatorio"), 
																	null, null, null,
																	obterLoginUsuarioLogado());
					
					documentos = digitalizacaoPOLFacade.buscaUrlsDocumentosGEDAdminstrativos(parametros);
				break;
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public DominioOrigemDocsDigitalizados getOrigemEnum() {
		return origemEnum;
	}

	public void setOrigemEnum(DominioOrigemDocsDigitalizados origemEnum) {
		this.origemEnum = origemEnum;
	}

	public List<DocumentoGEDVO> getDocumentos() {
		return documentos;
	}

	public void setDocumentos(List<DocumentoGEDVO> documentos) {
		this.documentos = documentos;
	}

	public DominioFichasDocsDigitalizados getFicha() {
		return ficha;
	}

	public void setFicha(DominioFichasDocsDigitalizados ficha) {
		this.ficha = ficha;
	}

	public NodoPOLVO getItemPOL() {
		return itemPOL;
	}

	public void setItemPOL(NodoPOLVO itemPOL) {
		this.itemPOL = itemPOL;
	}
}