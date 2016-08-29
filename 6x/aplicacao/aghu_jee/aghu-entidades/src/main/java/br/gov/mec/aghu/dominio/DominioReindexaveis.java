package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.model.AipCaixaPostalComunitarias;
import br.gov.mec.aghu.model.AipCidades;
import br.gov.mec.aghu.model.AipGrandesUsuarios;
import br.gov.mec.aghu.model.AipLogradouros;
import br.gov.mec.aghu.model.AipOcupacoes;
import br.gov.mec.aghu.model.AipSinonimosOcupacao;
import br.gov.mec.aghu.model.AipTipoLogradouros;
import br.gov.mec.aghu.model.AipUnidadeOperacao;
import br.gov.mec.aghu.model.MamOrigemPaciente;
import br.gov.mec.aghu.model.ScoMarcaComercial;
import br.gov.mec.aghu.model.ScoMarcaModelo;
import br.gov.mec.aghu.model.ScoMaterial;

public enum DominioReindexaveis {

	TODOS("Reindexar Todas"),
	AIPCIDADE(AipCidades.class.getCanonicalName()), 
	AIPTIPOLOGRADOURO(AipTipoLogradouros.class.getCanonicalName()), 
	AIPSINONIMOOCUPACAO(AipSinonimosOcupacao.class.getCanonicalName()), 
	AIPOCUPACOES(AipOcupacoes.class.getCanonicalName()), 
	AIPLOGRADOUROS(AipLogradouros.class.getCanonicalName()), 
	SCOMATERIAL(ScoMaterial.class.getCanonicalName()), 
	AIPUNIDADEOPERACAO(AipUnidadeOperacao.class.getCanonicalName()),
	AIPCAIXAPOSTALCOMUNITARIAS(AipCaixaPostalComunitarias.class.getCanonicalName()),
	AIPGRANDESUSUARIOS(AipGrandesUsuarios.class.getCanonicalName()),
	SCOMARCACOMERCIAL(ScoMarcaComercial.class.getCanonicalName()),
	SCOMARCAMODELO(ScoMarcaModelo.class.getCanonicalName()),
	MAMORIGEMPACIENTE(MamOrigemPaciente.class.getCanonicalName());
	
	private String descricao;

	private DominioReindexaveis(String descricao) {
		this.descricao = descricao;
	}

	public String toString() {
		return this.getDescricao();
	}
	
	public String getDescricao() {
		return descricao;
	}

}
