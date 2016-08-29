package br.gov.mec.aghu.action;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.inject.Named;

import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.casca.model.Modulo;
import br.gov.mec.aghu.dominio.DominioCor;
import br.gov.mec.aghu.core.action.ActionController;

@Named
public class AutoCompleteController extends ActionController {

    	private static final long serialVersionUID = 3009667631525057042L;

	@EJB
	private ICascaFacade cascaFacade;
	
	private User user;
	private int count;
	private String descricao;
	private List<User> userList;
	
	private Integer numeroInteiro;
	private Double numeroDecimal;
	
	private Double numeroDecimal2;
	
	private Byte numeroByte;
	
	private Long numeroLong;
	
	private Float numeroFloat;
	
	private BigDecimal numeroBigDecimal;
	
	private DominioCor cor;
	
	private Modulo modulo;
	
	private List<Modulo> modulos;
	
	private Date data;
	
	private Date datahora;
	
	private Date datahorasegundo;
	
	private Date hora;
	
	private Integer prontuario;
	
	private Long cpf;
	
	private Long cnpj;
	
	private Integer cep;
	
	private Boolean flag;
	
	private String testeInputText;
	
	public void init(){
		begin(conversation);		
		count=100;
		userList = returnUsers(null);
		cor = DominioCor.B;
		modulos = cascaFacade.listarModulosAtivos();
		modulo = modulos.get(0);
	}
	
	
	public List<User> returnUsers(String filtro) {
		return createList(filtro);
	}
	
	
	private List<User> createList(String filtro){
		List<User> userList = new ArrayList<User>();
		for (String loc : getLocations()) {
			if (filtro!=null){
				if (loc.toUpperCase().startsWith(filtro.toUpperCase())) {
					userList.add(new User(loc.hashCode() < 0 ? -1 * loc.hashCode()
							: loc.hashCode(), loc, new Date()));
					count++;
				}
			}else{
				userList.add(new User(loc.hashCode() < 0 ? -1 * loc.hashCode()
						: loc.hashCode(), loc, new Date()));
			}
		}
		
	//	return userList;
		return returnSGWithCount(userList, count(filtro));
		
	}
	
	public void somar(){
	    //TODO 
	    //System.out.println("testando");
	}
	
	
	public Long count(String param){
		return Long.valueOf(count);
	}
	
	
	public void posSelect(){
		descricao="Vou viajar para " + user.getCountry();
	}
	
	public void posDelete(){
		descricao="";
	}
	
	
	
	public String[] getLocations() {
		return new String[] { "Abari", "Absurdsvanj", "Adjikistan",
				"Afromacoland", "Agrabah", "Agaria", "Aijina", "Ajir",
				"Al-Alemand", "Al Amarja", "Alaine", "Albenistan", "Aldestan",
				"Al Hari", "Alpine Emirates", "Altruria",
				"Allied States of America", "BabaKiueria", "Babalstan",
				"Babar's Kingdom", "Backhairistan", "Bacteria", "Bahar",
				"Bahavia", "Bahkan", "Bakaslavia", "Balamkadar", "Baki",
				"Balinderry", "Balochistan", "Baltish", "Baltonia",
				"Bataniland, Republic of", "Bayview", "Banania, Republica de",
				"Bandrika", "Bangalia", "Bangstoff", "Bapetikosweti", "Baracq",
				"Baraza", "Barataria", "Barclay Islands", "Barringtonia",
				"Bay View", "Basenji", "aaaaa"};
	}

	public User getUser() {
		return user; 
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}


	public List<User> getUserList() {
		return userList;
	}


	public void setUserList(List<User> userList) {
		this.userList = userList;
	}


	public Integer getNumeroInteiro() {
		return numeroInteiro;
	}


	public void setNumeroInteiro(Integer numeroInteiro) {
		this.numeroInteiro = numeroInteiro;
	}


	public Double getNumeroDecimal() {
		return numeroDecimal;
	}


	public void setNumeroDecimal(Double numeroDecimal) {
		this.numeroDecimal = numeroDecimal;
	}


	public Double getNumeroDecimal2() {
		return numeroDecimal2;
	}


	public void setNumeroDecimal2(Double numeroDecimal2) {
		this.numeroDecimal2 = numeroDecimal2;
	}


	public Byte getNumeroByte() {
		return numeroByte;
	}


	public void setNumeroByte(Byte numeroByte) {
		this.numeroByte = numeroByte;
	}


	public Long getNumeroLong() {
		return numeroLong;
	}


	public void setNumeroLong(Long numeroLong) {
		this.numeroLong = numeroLong;
	}


	public Float getNumeroFloat() {
		return numeroFloat;
	}


	public void setNumeroFloat(Float numeroFloat) {
		this.numeroFloat = numeroFloat;
	}


	public BigDecimal getNumeroBigDecimal() {
		return numeroBigDecimal;
	}


	public void setNumeroBigDecimal(BigDecimal numeroBigDecimal) {
		this.numeroBigDecimal = numeroBigDecimal;
	}


	public DominioCor getCor() {
		return cor;
	}


	public void setCor(DominioCor cor) {
		this.cor = cor;
	}


	public Modulo getModulo() {
		return modulo;
	}


	public void setModulo(Modulo modulo) {
		this.modulo = modulo;
	}


	public List<Modulo> getModulos() {
		return modulos;
	}


	public void setModulos(List<Modulo> modulos) {
		this.modulos = modulos;
	}


	public Date getData() {
		return data;
	}


	public void setData(Date data) {
		this.data = data;
	}


	public Date getDatahora() {
		return datahora;
	}


	public void setDatahora(Date datahora) {
		this.datahora = datahora;
	}


	public Date getDatahorasegundo() {
		return datahorasegundo;
	}


	public void setDatahorasegundo(Date datahorasegundo) {
		this.datahorasegundo = datahorasegundo;
	}


	public Date getHora() {
		return hora;
	}


	public void setHora(Date hora) {
		this.hora = hora;
	}


	public Integer getProntuario() {
		return prontuario;
	}


	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
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


	public Integer getCep() {
		return cep;
	}


	public void setCep(Integer cep) {
		this.cep = cep;
	}


	public Boolean getFlag() {
		return flag;
	}


	public void setFlag(Boolean flag) {
		this.flag = flag;
	}


	public String getTesteInputText() {
		return testeInputText;
	}


	public void setTesteInputText(String testeInputText) {
		this.testeInputText = testeInputText;
	}



}
