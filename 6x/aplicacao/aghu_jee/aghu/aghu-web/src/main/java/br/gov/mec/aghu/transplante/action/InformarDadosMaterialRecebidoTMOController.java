package br.gov.mec.aghu.transplante.action;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.dominio.DominioSituacaoTmo;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MtxColetaMaterialTmo;
import br.gov.mec.aghu.model.MtxTransplantes;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.transplante.business.ITransplanteFacade;
import br.gov.mec.aghu.transplante.vo.ResultadoExameCulturalVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.Severity;

public class InformarDadosMaterialRecebidoTMOController extends ActionController {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3270081385632757335L;
	
	@EJB
	private ITransplanteFacade transplanteFacade;
	@EJB
	protected IRegistroColaboradorFacade registroColaboradorFacade;
	
	private AipPacientes aipPacientes = new AipPacientes();
	private MtxColetaMaterialTmo coletaMaterialTmo = new MtxColetaMaterialTmo();
	private MtxTransplantes mtxTransplantes;
	private List<ResultadoExameCulturalVO> listaRECVO;
	private Integer material;
	private BigDecimal cd34PorKg;
	private BigDecimal celulaNucleadaPorKg;
	private String cd34CarregadoPreviamente;
	private BigDecimal leucocitosTotaisCarregadosPreviamente;
	private BigDecimal leucocitos = BigDecimal.ZERO;
	private Integer codTransplante;
	private String voltarPara;
	private String prontuario;
	private Boolean isEdicao = Boolean.FALSE;
	private Boolean isInsercao = Boolean.FALSE;
	private Boolean habilitaCD34 = Boolean.FALSE;
	private Boolean habilitaLeucocitosTotais = Boolean.FALSE;
	private Boolean habilitarExameCultural = Boolean.FALSE;

	@PostConstruct
	public void init() {
		begin(conversation);
	}
	
	public void iniciar(){
//		codTransplante = 1;
		if(codTransplante != null){
			//EXECUTA A C1 PARA PREENCHER O PRONTUARIO E O NOME DO PACIENTE
			aipPacientes = transplanteFacade.buscarPacientePorCodTransplante(codTransplante);
			if (this.aipPacientes != null && this.aipPacientes.getProntuario() != null) {
				this.prontuario = this.formatarMascaraProntuario(this.aipPacientes.getProntuario());
			} else {
				this.prontuario = null;
			}
			//EXECUTA C6 PARA VERIFICAR SE EH EDICAO OU INSERCAO
			coletaMaterialTmo = transplanteFacade.buscarColetaMaterialTMOPorTransplanteSeq(codTransplante);
			//INSTANCIA UM OBJETO A SER UTILIZADO CASO A CONSULTA C6 RETORNE "NULL"
			if(coletaMaterialTmo == null){
				coletaMaterialTmo = new MtxColetaMaterialTmo();
				isInsercao = Boolean.TRUE;
			}else{
				isEdicao = Boolean.TRUE;
				material = coletaMaterialTmo.getCodMaterial();
			}
			//EXECUTA A C5
			mtxTransplantes = transplanteFacade.obterTransplantePorSeq(codTransplante);
			//SE C5 OBTIVER RESULTADOS
			validarC2eC3();
			//PREENCHE/HABILITA_EDICAO CAMPO EXAME_CULTURAL
			atualizarExameCultural();
		}
	}

	private void validarC2eC3() {
		Integer codDoadorReceptor = null;
		if(mtxTransplantes != null){
			if(DominioSituacaoTmo.U.equals(mtxTransplantes.getTipoTmo()) && mtxTransplantes.getReceptor() != null){//Autólogo
				codDoadorReceptor = mtxTransplantes.getReceptor().getCodigo();
			}else if(mtxTransplantes.getDoador() != null){//Alogênico
				codDoadorReceptor = mtxTransplantes.getDoador().getCodigo();
			}
			if(codDoadorReceptor != null){
				cd34CarregadoPreviamente = transplanteFacade.buscarValorCampoCD34(codDoadorReceptor, coletaMaterialTmo.getCodMaterial());
				leucocitos = transplanteFacade.buscarValorLeucocitosTotais(codDoadorReceptor);
			}
			
			if(cd34CarregadoPreviamente != null && !StringUtils.isEmpty(cd34CarregadoPreviamente)){//caso C3 não retorne nada, utiliza MTX_COLETA_MATERIAIS_TMO.CD34, caso exista, e habilita o campo para edição
				coletaMaterialTmo.setCd34(new BigDecimal(cd34CarregadoPreviamente.replace(",", ".")));
			}else{
				habilitaCD34 = Boolean.TRUE;
			}
			
			if(leucocitos != null && !BigDecimal.ZERO.equals(leucocitos)){
				leucocitos = leucocitos.divide(new BigDecimal(100));
			}else {//Caso C2 não retorne nada, utiliza MTX_COLETA_MATERIAIS_TMO.LEUCOCITOS, caso exista, e habilita o campo para edição
				if(coletaMaterialTmo.getLeucocitos() != null && !BigDecimal.ZERO.equals(coletaMaterialTmo.getLeucocitos())){
					leucocitos = new BigDecimal(coletaMaterialTmo.getLeucocitos()).divide(new BigDecimal(100));
				}
				habilitaLeucocitosTotais = Boolean.TRUE;
			}
		}
		efetuarCalculos();
	}
		
	private String formatarMascaraProntuario(Integer prontuario) {
		
		String valor = prontuario.toString();
		int tamanho = valor.length();
		String mascara = "";
		
		for (int i = 0; i < tamanho; i++) {
			if (i == tamanho-1) {
				mascara = mascara.concat("-#"); // ULTIMO DIGITO
			} else {
				mascara = mascara.concat("#"); // DEMAIS DIGITOS
			}
		}
		
		return this.inserirMascara(valor, mascara);
	}
	
	/**
     * Insere a máscara de formatação no valor da String informada.<br /><tt>Ex.: inserirMascara("11111111111",
     * "###.###.###-##")</tt>.
     * 
     * @param valor {@link String} que será manipulada.
     * @param mascara Máscara que será aplicada.
     * @return Valor com a máscara de formatação.
     */
    private String inserirMascara(String valor, String mascara) {

        String novoValor = "";
        int posicao = 0;

        for (int i = 0; mascara.length() > i; i++) {
            if (mascara.charAt(i) == '#') {
                if (valor.length() > posicao) {
                    novoValor = novoValor.concat(String.valueOf(valor.charAt(posicao)));
                    posicao++;
                } else {
                    break;
                }
            } else {
                if (valor.length() > posicao) {
                    novoValor = novoValor.concat(String.valueOf(mascara.charAt(i)));
                } else {
                    break;
                }
            }
        }
        return novoValor;
    }
	
	public String voltar(){
		this.limparCampos();
		return this.voltarPara;
	}

	public void gravar(){
		if(coletaMaterialTmo != null){
			try {
				transplanteFacade.validarCamposObrigatoriosColetaMaterialTmo(coletaMaterialTmo);
				RapServidores servidor = registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado());
				
				if(leucocitos != null && !BigDecimal.ZERO.equals(leucocitos)){
					coletaMaterialTmo.setLeucocitos(leucocitos.multiply(new BigDecimal(100)).intValue());
				}
				coletaMaterialTmo.setCodMaterial(material);
				coletaMaterialTmo.setServidor(servidor);
				coletaMaterialTmo.setPacCodigo(aipPacientes);
				coletaMaterialTmo.setTransplante(mtxTransplantes);
				if(isEdicao){
					transplanteFacade.atualizarColetaMaterialTMO(coletaMaterialTmo);
					apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EDICAO_TMO");
				} else if (isInsercao){
					coletaMaterialTmo.setCriadoEm(new Date());
					transplanteFacade.gravarColetaMaterialTMO(coletaMaterialTmo);
					apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_INSERCAO_TMO");
				}
				
			} catch (BaseListException e) {
				apresentarExcecaoNegocio(e);
			} catch(ApplicationBusinessException e){
				apresentarExcecaoNegocio(e);
			}
		}
	}
	
	public void efetuarCalculos(){
		if(coletaMaterialTmo != null){
			validarCampos();
			if(coletaMaterialTmo.getVolume() != null && leucocitos != null && coletaMaterialTmo.getPeso() != null){
				BigDecimal volumeXLeucocitos = new BigDecimal(coletaMaterialTmo.getVolume()).multiply(leucocitos);
				celulaNucleadaPorKg = volumeXLeucocitos.divide(coletaMaterialTmo.getPeso(), 2, RoundingMode.HALF_UP);
			}
			
			if(coletaMaterialTmo.getCd34() != null && celulaNucleadaPorKg != null 
					&& coletaMaterialTmo.getVolume() != null && leucocitos != null && coletaMaterialTmo.getPeso() != null){
				cd34PorKg = celulaNucleadaPorKg.multiply(coletaMaterialTmo.getCd34().divide(new BigDecimal(100)));
			}
		}
	}
	
	private void validarCampos(){
		if(coletaMaterialTmo.getCd34() != null || coletaMaterialTmo.getVolume() == null 
				|| leucocitos == null || coletaMaterialTmo.getPeso() == null){
			cd34PorKg = null;
		}
		if(coletaMaterialTmo.getVolume() == null || leucocitos == null || coletaMaterialTmo.getPeso() == null){
			celulaNucleadaPorKg = null;
			cd34PorKg = null;
		}
		
	}
	public void atualizarExameCultural(){
		
		if(coletaMaterialTmo != null && coletaMaterialTmo.getCodMaterial() != null){
			if(mtxTransplantes.getDoador() != null){
				if(mtxTransplantes.getDoador().getCodigo()!=null){
					listaRECVO = transplanteFacade.obterListaExameCultural(mtxTransplantes.getDoador().getCodigo(), coletaMaterialTmo.getCodMaterial());
				}
			}
		}
		List<ResultadoExameCulturalVO> listaRECVOaux = new ArrayList<ResultadoExameCulturalVO>();
		
		if(listaRECVO != null){
			listaRECVOaux.addAll(listaRECVO);
			if(listaRECVO.size() > 1){
				for(ResultadoExameCulturalVO obj : listaRECVO){
					for(ResultadoExameCulturalVO obj2 : listaRECVOaux){
						if(obj.getDthrLiberada().compareTo(obj2.getDthrLiberada()) > 0){
							coletaMaterialTmo.setExameCultural(obj.getDescricao());
						} else {
							coletaMaterialTmo.setExameCultural(obj2.getDescricao());
						}
					}
				}
			} else if(listaRECVO.size() == 1) {
				coletaMaterialTmo.setExameCultural(listaRECVO.get(0).getDescricao());
				habilitarExameCultural = Boolean.FALSE;
			}
		} else {
			habilitarExameCultural = Boolean.TRUE;
		}
	}
	
	public void limparCampos(){
		isEdicao = Boolean.FALSE;
		isInsercao = Boolean.FALSE;
		habilitaCD34 = Boolean.FALSE;
		habilitaLeucocitosTotais = Boolean.FALSE;
		habilitarExameCultural = Boolean.FALSE;
		coletaMaterialTmo = new MtxColetaMaterialTmo();
		leucocitosTotaisCarregadosPreviamente = null;
		leucocitos = BigDecimal.ZERO;
		material = null;
		cd34PorKg = null;
		celulaNucleadaPorKg = null;
		listaRECVO = null;
		cd34CarregadoPreviamente = null;
	}
	
	/*GETTERS & SETTERS*/
	
	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public AipPacientes getAipPacientes() {
		return aipPacientes;
	}

	public void setAipPacientes(AipPacientes aipPacientes) {
		this.aipPacientes = aipPacientes;
	}

	public Integer getMaterial() {
		return material;
	}

	public void setMaterial(Integer material) {
		this.material = material;
		if(coletaMaterialTmo != null){
			coletaMaterialTmo.setCodMaterial(material);
		}
	}

	public MtxColetaMaterialTmo getColetaMaterialTmo() {
		return coletaMaterialTmo;
	}

	public void setColetaMaterialTmo(MtxColetaMaterialTmo coletaMaterialTmo) {
		this.coletaMaterialTmo = coletaMaterialTmo;
	}

	public Integer getCodTransplante() {
		return codTransplante;
	}

	public void setCodTransplante(Integer codTransplante) {
		this.codTransplante = codTransplante;
	}

	public BigDecimal getCd34PorKg() {
		return cd34PorKg;
	}

	public void setCd34PorKg(BigDecimal cd34PorKg) {
		this.cd34PorKg = cd34PorKg;
	}

	public BigDecimal getCelulaNucleadaPorKg() {
		return celulaNucleadaPorKg;
	}

	public void setCelulaNucleadaPorKg(BigDecimal celulaNucleadaPorKg) {
		this.celulaNucleadaPorKg = celulaNucleadaPorKg;
	}

	public Boolean getIsEdicao() {
		return isEdicao;
	}

	public void setIsEdicao(Boolean isEdicao) {
		this.isEdicao = isEdicao;
	}

	public Boolean getIsInsercao() {
		return isInsercao;
	}

	public void setIsInsercao(Boolean isInsercao) {
		this.isInsercao = isInsercao;
	}

	public MtxTransplantes getMtxTransplantes() {
		return mtxTransplantes;
	}

	public void setMtxTransplantes(MtxTransplantes mtxTransplantes) {
		this.mtxTransplantes = mtxTransplantes;
	}

	public String getCd34CarregadoPreviamente() {
		return cd34CarregadoPreviamente;
	}

	public void setCd34CarregadoPreviamente(String cd34CarregadoPreviamente) {
		this.cd34CarregadoPreviamente = cd34CarregadoPreviamente;
	}

	public Boolean getHabilitaCD34() {
		return habilitaCD34;
	}

	public void setHabilitaCD34(Boolean habilitaCD34) {
		this.habilitaCD34 = habilitaCD34;
	}

	public BigDecimal getLeucocitosTotaisCarregadosPreviamente() {
		return leucocitosTotaisCarregadosPreviamente;
	}

	public void setLeucocitosTotaisCarregadosPreviamente(
			BigDecimal leucocitosTotaisCarregadosPreviamente) {
		this.leucocitosTotaisCarregadosPreviamente = leucocitosTotaisCarregadosPreviamente;
	}

	public Boolean getHabilitaLeucocitosTotais() {
		return habilitaLeucocitosTotais;
	}

	public void setHabilitaLeucocitosTotais(Boolean habilitaLeucocitosTotais) {
		this.habilitaLeucocitosTotais = habilitaLeucocitosTotais;
	}

	public List<ResultadoExameCulturalVO> getListaRECVO() {
		return listaRECVO;
	}

	public void setListaRECVO(List<ResultadoExameCulturalVO> listaRECVO) {
		this.listaRECVO = listaRECVO;
	}

	public Boolean getHabilitarExameCultural() {
		return habilitarExameCultural;
	}

	public void setHabilitarExameCultural(Boolean habilitarExameCultural) {
		this.habilitarExameCultural = habilitarExameCultural;
	}

	public String getProntuario() {
		return prontuario;
	}

	public void setProntuario(String prontuario) {
		this.prontuario = prontuario;
	}

	public BigDecimal getLeucocitos() {
		return leucocitos;
	}

	public void setLeucocitos(BigDecimal leucocitos) {
		this.leucocitos = leucocitos;
	}
}