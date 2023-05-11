import { COMUNI } from './lista-comuni.js'
import { normalizeString } from './utils.js'
export class Comune {
  get nomeNorm () {
    return normalizeString(this.nome)
  }
  constructor (nome, prov, cc, istat, check = true) {
    if (check || cc === undefined || prov === undefined) {
      let comune = this.searchByNameAndProvince(nome, prov, istat)
      
      if (comune === undefined && nome.length === 4) {
        comune = this.searchByCC(nome)
      }

      if (comune === undefined) {
        throw new Error(`LABEL_ERRORE_COMUNE_NOME_NON_ESISTE|${nome}`)
      } else if (cc !== undefined && comune.cc !== cc) {
        throw new Error(`LABEL_ERRORE_COMUNE_CC_NON_ESISTE|${cc}`)
      } else {
        this.nome = comune.nome
        this.prov = comune.prov
        this.cc = comune.cc
      }
    } else {
      this.nome = nome
      this.prov = prov
      this.cc = cc
    }
  }
  static GetByName (name, prov) {
    return new Comune(name, prov)
  }
  static GetByCC (cc) {
    let result
    let count = 0
    for (const item of COMUNI) {
      if ( item[0] === cc && item[3]===1) {
        result = item
        break
      } else if(item[0] === cc){
        result = item;
      }
    }
    if (result !== undefined) {
      return new Comune(result[2], result[1], result[0], undefined, false)
    }
    throw new Error(`LABEL_ERRORE_COMUNE_CC_NON_ESISTE|${cc}`)
  }

  searchByCC (cc) {
    let result
    try {
      result = Comune.GetByCC(cc)
    } catch (e) { }
    if (result !== undefined) {
      return result.toJSON()
    }
  }
  searchByName (nome ) {
    this.searchByNameAndProvince(nome)
  }
  searchByNameAndProvince (nome, prov, istat) {
    const qNome = normalizeString(nome)
    const qProv= prov && normalizeString(prov) 
    let results = COMUNI.filter((c) =>
            qProv
            ? c[1] === qProv && c[2]===qNome && (istat == undefined || c[0] == istat)
            : c[2]===qNome)
        .map((c)=>{
            return { cc: c[0], prov: c[1], nome: c[2], active:c[3]===1 }
        })
    
    // One results: no problem!
    if (results.length === 1)
      return results[0]

    // if many results look for the active one
    let activeResults = results.filter(c=> c.active)
    
    if(activeResults.length === 1)  //1 Soloro risultato e attivo (ideale)
        return results[0]
    else if (activeResults.length == 0 && results.length == 1)    //Trovato uno o più comuni, ma, nessuno di questi è attivo
        return results[0];
    else if(activeResults.length == 0 && results.length == 0 && prov)   //Non ho trovato alcun risalto (attivo o meno)
        throw new Error(`LABEL_ERRORE_COMUNE_NOME_E_PROV_NON_ESISTE|${nome}|${prov}`)
    else if(nome.length===4 && nome.toUpperCase() === nome)
        return Comune.GetByCC(nome)
    else if(activeResults.length == 0 && results.length > 1 && prov)    //Più di un risultato e nessuno attivo
        throw new Error(`LABEL_ERRORE_COMUNE_MULTIPLO|${nome}`)
    else    //Nessun risultato
        throw new Error(`LABEL_ERRORE_COMUNE_NOME_NON_ESISTE|${nome}`)
    
  }

  toJSON () {
    return {
      cc: this.cc,
      nome: this.nome,
      prov: this.prov
    }
  }
}
